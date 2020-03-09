package algebras

import Model._
import cats.InjectK
import cats.free.Free
import cats.free.Free._

object Invoices {

  sealed trait InvoiceOp[A]

  case class Create(amount: Int, accountId: AccountId, date: String)
      extends InvoiceOp[InvoiceId]
  case class Find(invoiceId: InvoiceId)
      extends InvoiceOp[Invoice]
  case class FindUnpaid(accountId: AccountId, date: String)
      extends InvoiceOp[List[Invoice]]

  type InvoiceOpF[A] = Free[InvoiceOp, A]

  object Implicits {
    implicit def invoiceOpI[F[_]](implicit I: InjectK[InvoiceOp, F]) = 
      new InvoiceOpI 
  }

  class InvoiceOpI[F[_]](implicit I: InjectK[InvoiceOp, F]) {
    def create(amount: Int, accountId: AccountId, date: String): Free[F, InvoiceId] =
      inject[InvoiceOp, F](Create(amount, accountId, date))

    def find(invoiceId: InvoiceId): Free[F, Invoice] =
      inject[InvoiceOp, F](Find(invoiceId))

    def findUnpaid(accountId: AccountId, date: String): Free[F, List[Invoice]] =
      inject[InvoiceOp, F](FindUnpaid(accountId, date))
     
  }
}
