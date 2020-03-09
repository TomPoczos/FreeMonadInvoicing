package algebras

import cats.InjectK
import cats.free.Free
import cats.free.Free._

object Migrations {


  sealed trait Migration[A]

  case object EnableForeignkeys extends Migration[Unit]

  case object CreateAccountTable extends Migration[Unit]

  case object CreateInvoiceTable extends Migration[Unit]

  case object CreatePaymentTable extends Migration[Unit]

  case object CreateBalancetable extends Migration[Unit]

  type MigrationF[A] = Free[Migration, A]

  class MigrationI[F[_]](implicit I: InjectK[Migration, F]) {

    def enableForeignkeys: Free[F, Unit]=
      inject[Migration, F](EnableForeignkeys)

    def createAccountTable: Free[F, Unit]=
      inject[Migration, F](CreateAccountTable)

    def createInvoiceTable: Free[F, Unit]=
      inject[Migration, F](CreateInvoiceTable)

    def createPaymentTable: Free[F, Unit]=
      inject[Migration, F](CreatePaymentTable)

    def createBalancetable: Free[F, Unit] =
      inject[Migration, F](CreateBalancetable)
  }

  object Implicits {
    implicit def migrationI[F[_]](implicit I: InjectK[Migration, F]) = new MigrationI
  }
}