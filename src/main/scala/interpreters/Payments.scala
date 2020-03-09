package interpreters

import Model._
import cats.implicits._
import cats.~>
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import algebras.Payments._

object Payments {

  val interpreter: PaymentOp ~> ConnectionIO =
    λ[PaymentOp ~> ConnectionIO](
      _ match {
        case Create(amount, InvoiceId(invoiceId), date) =>
          sql"""
               | insert into payment (amount, invoice_fk, date)
               | values ($amount, $invoiceId, $date)
               |""".stripMargin.update.run *>
            sql"select last_insert_rowid()".query[PaymentId].unique
      }
    )

    object implicits {

        implicit class PaymentOpInterpreterOps[A](paymentOp: PaymentOpF[A]) {
            def interpret = paymentOp.foldMap(interpreter)
        }
    }
}
