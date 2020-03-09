package algebras

import Model._
import algebras.Accounts.{AccountOp, AccountOpI}
import algebras.Balances.{BalanceOp, BalanceOpI}
import algebras.Invoices.{InvoiceOp, InvoiceOpI}
import algebras.Migrations.{Migration, MigrationI}
import algebras.Payments.{PaymentOp, PaymentOpI}
import cats.data.EitherK
import cats.free.Free
import cats.implicits._

object InvoiceApp {

  type AlgebraCoproduct[A] = EitherK[
    BalanceOp,
    EitherK[
      Migration,
      EitherK[
        AccountOp,
        EitherK[
          PaymentOp,
          InvoiceOp,
          *
        ],
        *
      ],
      *
    ],
    A
  ]

  type InvoiceAppF[B] = Free[AlgebraCoproduct, B]

  def runMigrations(
      implicit migration: MigrationI[AlgebraCoproduct]
  ): InvoiceAppF[Unit] =
    migration.enableForeignkeys *>
      migration.createAccountTable *>
      migration.createInvoiceTable *>
      migration.createPaymentTable *>
      migration.createBalancetable

  def createAccount(date: String)(
      implicit balance: BalanceOpI[AlgebraCoproduct],
      account: AccountOpI[AlgebraCoproduct]
  ): InvoiceAppF[AccountId] =
    for {
      accountId <- account.create
      _ <- balance.create(0, accountId, date)
    } yield accountId

  def createInvoce(amount: Int, accountId: AccountId, date: String)(
      implicit invoice: InvoiceOpI[AlgebraCoproduct],
      balance: BalanceOpI[AlgebraCoproduct]
  ): InvoiceAppF[InvoiceId] =
    for {
      invoiceId <- invoice.create(amount, accountId, date)
      lastBalance <- balance.findLast(accountId)
      _ <- balance.create(lastBalance.amount - amount, accountId, date)
    } yield invoiceId

  def findLastBalanceForDate(accountId: AccountId, date: String)(
      implicit balance: BalanceOpI[AlgebraCoproduct]
  ) = balance.findLastForDate(accountId, date)

  def payInvoice(
      invoiceId: InvoiceId,
      date: String
  )(
      implicit payment: PaymentOpI[AlgebraCoproduct],
      balance: BalanceOpI[AlgebraCoproduct],
      invoice: InvoiceOpI[AlgebraCoproduct]
  ): InvoiceAppF[PaymentId] =
    for {
      theInvoice <- invoice.find(invoiceId)
      paymentId <- payment.create(theInvoice.amount, invoiceId, date)
      lastBalance <- balance.findLast(theInvoice.accountId)
      _ <- balance.create(
        lastBalance.amount + theInvoice.amount,
        theInvoice.accountId,
        date
      )
    } yield paymentId

  def findUnpaid(accountId: AccountId, date: String)(
      implicit invoice: InvoiceOpI[AlgebraCoproduct]
  ): InvoiceAppF[List[Invoice]] =
    invoice.findUnpaid(accountId, date)

}
