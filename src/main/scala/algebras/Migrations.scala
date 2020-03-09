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

  object Migration {

    type MigrationF[A] = Free[Migration, A]

    def enableForeignkeys: MigrationF[Unit] =
      liftF[Migration, Unit](EnableForeignkeys)

    def createAccountTable: MigrationF[Unit] =
      liftF[Migration, Unit](CreateAccountTable)

    def createInvoiceTable: MigrationF[Unit] =
      liftF[Migration, Unit](CreateInvoiceTable)

    def createPaymentTable: MigrationF[Unit] =
      liftF[Migration, Unit](CreatePaymentTable)

    def createBalancetable: MigrationF[Unit] =
      liftF[Migration, Unit](CreateBalancetable)
  }

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