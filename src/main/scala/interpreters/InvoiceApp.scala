package interpreters

import algebras.InvoiceApp.InvoiceAppF
import cats.effect.Sync
import cats.~>
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

object InvoiceApp {

  def interpreter =
    Balances.interpreter or (
      Migrations.interpreter or (
        Accounts.interpreter or (
          Payments.interpreter or
            Invoices.interpreter
        )
      )
    )

  def ioInterpreter[F[_]: Sync: Transactor]: ConnectionIO ~> F =
    λ[ConnectionIO ~> F](_.transact(F)(F))

  object Implicits {

    implicit class InvoiceAppInterpreterOps[A](
        invoiceApp: InvoiceAppF[A]
    ) {
      def interpret[F[_]: Sync: Transactor] = 
      invoiceApp.foldMap(interpreter).transact(F)(F)
      // F.delay(println("Transaction")) *> invoiceApp.foldMap(interpreter).transact(F)(F)
    }
  }
}
