package interpreters

import Model.{Invoice, _}
import algebras.Invoices.InvoiceOp.InvoiceOpF
import algebras.Invoices._
import cats.implicits._
import cats.~>
import doobie.free.connection.ConnectionIO
import doobie.implicits._

object Invoices {

  val interpreter: InvoiceOp ~> ConnectionIO =
    λ[InvoiceOp ~> ConnectionIO](
      _ match {
        case Create(amount, AccountId(accId), date) =>
          sql"""
               |insert into invoice(amount, account_fk, date)
               |values ($amount, $accId, $date)
               |""".stripMargin.update.run *>
            sql"select last_insert_rowid()".query[InvoiceId].unique
        case Find(InvoiceId(invoiceId)) =>
          sql"""
               | select id, amount, account_fk, date
               | from invoice where id = $invoiceId
               |""".stripMargin.query[Invoice].unique
        case FindUnpaid(AccountId(accId), date) =>
            sql"""
                 | select i.id, i.amount, i.account_fk, i.date 
                 | from invoice i
                 | where i.account_fk = $accId
                 | and i.date <= $date
                 | and i.id not in (
                 |     select p.invoice_fk 
                 |     from payment p
                 |     where p.invoice_fk = i.id
                 |     and p.date <= $date
                 | )
                 |""".stripMargin.query[Invoice].to[List]

                //              sql"""
                //  | select i.id, i.amount, i.account_fk, i.date 
                //  | from invoice i
                //  | where i.account_fk = $accId
                //  | and date(i.date) < date($date)
                //  | and i.id not in (
                //  |     select p.invoice_fk 
                //  |     from payment p
                //  |     where p.invoice_fk = $accId
                //  |     and date(p.date) < date($date)
                //  | )
                //  |""".stripMargin.query[Invoice].to[List]
      }
    )

  object Implicits {
    implicit class InvoiceInterpreterops[A](invoice: InvoiceOpF[A]) {
      def interpret = invoice.foldMap(interpreter)
    }
  }
}
