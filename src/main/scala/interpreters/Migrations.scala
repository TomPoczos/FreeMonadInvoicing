package interpreters

import algebras.Migrations.Migration._
import algebras.Migrations.{Migration, _}
import cats.~>
import doobie.free.connection.ConnectionIO
import doobie.implicits._

object Migrations {

  val interpreter: Migration ~> ConnectionIO =
    λ[Migration ~> ConnectionIO](
      _ match {
        case EnableForeignkeys =>
          sql"""
               |PRAGMA foreign_keys = ON
               |""".stripMargin.update.run.map(_ => ())
        case CreateAccountTable =>
          sql"""
               |create table if not exists account(
               |  id integer primary key
               |)
               |""".stripMargin.update.run.map(_ => ())
        case CreateInvoiceTable =>
          sql"""
               |create table if not exists invoice(
               |  id integer primary key,
               |  amount integer not null,
               |  account_fk integer not null references account(id),
               |  date text not null
               |)
               |""".stripMargin.update.run.map(_ => ())
        case CreatePaymentTable =>
          sql"""
               |create table if not exists payment(
               |  id integer primary key,
               |  amount integer not null,
               |  invoice_fk integer not null unique references invoice(id),
               |  date text not null
               |)
               |""".stripMargin.update.run.map(_ => ())
        case CreateBalancetable => 
          sql"""
               |create table if not exists balance(
               |  id integer primary key,
               |  amount integer not null,
               |  account_fk integer not null references account(id),
               |  date text not null
               |)
               |""".stripMargin.update.run.map(_ => ())
      }
    )

  object Implicits {
    implicit class MigrationInterpreterOps[A](migration: MigrationF[A]) {
      def interpret = migration.foldMap(interpreter)
    }
  }

}
