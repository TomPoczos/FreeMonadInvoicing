import Configuration.DbConfig
import algebras.Accounts.Implicits._
import algebras.Balances.Implicits._
import algebras.Invoices.Implicits._
import algebras.InvoiceApp._
import algebras.Migrations.Implicits._
import algebras.Payments.Implicits._
import cats.effect.{ExitCode, IO, IOApp}
import doobie.Transactor
import interpreters.InvoiceApp.Implicits._


object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    implicit val transactor = Transactor.fromDriverManager[IO](
      DbConfig.dbDriver,
      DbConfig.dbUrl
    )

    // the whole program can be interpreted in one go as seen in the commented out code below,
    // but that would mean that all the code runs in the same transaction. By interpreting
    // every command separately proper transaction boundaries can be enforced

    for {
      
      _ <- runMigrations.interpret[IO]
      
      accountId1 <- createAccount( "2019-12-31T01:00:00.000Z").interpret[IO]
      
      invoiceId1forAccount1 <- createInvoce(1, accountId1, "2020-01-01T01:00:00.000Z").interpret[IO]
      invoiceId2forAccount1 <- createInvoce(10, accountId1, "2020-01-02T01:00:00.000Z").interpret[IO]
      invoiceId3forAccount1 <- createInvoce(100, accountId1, "2020-01-03T01:00:00.000Z").interpret[IO]
      invoiceId4forAccount1 <- createInvoce(1000, accountId1, "2020-01-04T01:00:00.000Z").interpret[IO]

      paymentId1forAccount1 <- payInvoice(invoiceId2forAccount1, "2020-01-04T02:00:00.000Z").interpret[IO]

      invoiceId5forAccount1 <- createInvoce(10000, accountId1, "2020-01-05T01:00:00.000Z").interpret[IO]

      accountId2 <- createAccount( "2019-12-31T01:00:00.000Z").interpret[IO]
      
      invoiceId1forAccount2 <- createInvoce(2, accountId2, "2020-02-01T01:00:00.000Z").interpret[IO]
      invoiceId2forAccount2 <- createInvoce(20, accountId2, "2020-02-02T01:00:00.000Z").interpret[IO]
      invoiceId3forAccount2 <- createInvoce(200, accountId2, "2020-02-03T01:00:00.000Z").interpret[IO]
      invoiceId4forAccount2 <- createInvoce(2000, accountId2, "2020-02-04T01:00:00.000Z").interpret[IO]
      
      paymentId1forAccount2 <- payInvoice(invoiceId3forAccount2, "2020-01-04T02:00:00.000Z").interpret[IO]
      
      invoiceId5forAccount2 <- createInvoce(20000, accountId2, "2020-02-05T01:00:00.000Z").interpret[IO]

      unpaidInvoice1 <- findUnpaid(accountId1, "2020-01-01T23:59:59.000Z").interpret[IO]
      unpaidInvoice2 <- findUnpaid(accountId1, "2020-01-02T23:59:59.000Z").interpret[IO]
      unpaidInvoice3 <- findUnpaid(accountId1, "2020-01-03T23:59:59.000Z").interpret[IO]
      unpaidInvoice4 <- findUnpaid(accountId1, "2020-01-04T23:59:59.000Z").interpret[IO]
      unpaidInvoice5 <- findUnpaid(accountId1, "2020-01-05T23:59:59.000Z").interpret[IO]

      balance1 <- findLastBalanceForDate(accountId1, "2020-01-01T23:59:59.000Z").interpret[IO]
      balance2 <- findLastBalanceForDate(accountId1, "2020-01-02T23:59:59.000Z").interpret[IO]
      balance3 <- findLastBalanceForDate(accountId1, "2020-01-03T23:59:59.000Z").interpret[IO]
      balance4 <- findLastBalanceForDate(accountId1, "2020-01-04T23:59:59.000Z").interpret[IO]
      balance5 <- findLastBalanceForDate(accountId1, "2020-01-05T23:59:59.000Z").interpret[IO]
      
      _ <- IO(println(unpaidInvoice1))
      _ <- IO(println(unpaidInvoice2))
      _ <- IO(println(unpaidInvoice3))
      _ <- IO(println(unpaidInvoice4))
      _ <- IO(println(unpaidInvoice5))

      _ <- IO(println(balance1))
      _ <- IO(println(balance2))
      _ <- IO(println(balance3))
      _ <- IO(println(balance4))
      _ <- IO(println(balance5))
    } yield ExitCode.Success

    // val program: InvoiceAppF[(AccountId, Account.Balance)] =
    //   for {
    //     _                     <- runMigrations

    //     accountId1            <- createAccount

    //     invoiceId1forAccount1 <- createInvoce(1, accountId1, "2020-1-1")
    //     invoiceId2forAccount1 <- createInvoce(10, accountId1, "2020-1-2")
    //     invoiceId3forAccount1 <- createInvoce(100, accountId1, "2020-1-3")
    //     invoiceId4forAccount1 <- createInvoce(1000, accountId1, "2020-1-4")
    //     invoiceId5forAccount1 <- createInvoce(10000, accountId1, "2020-1-5")

    //     paymentId1forAccount1 <- payInvoice(invoiceId3forAccount1, "2020-1-4")

    //     accountId2            <- createAccount

    //     invoiceId1forAccount2 <- createInvoce(2, accountId2, "2020-2-1")
    //     invoiceId2forAccount2 <- createInvoce(20, accountId2, "2020-2-2")
    //     invoiceId3forAccount2 <- createInvoce(200, accountId2, "2020-2-3")
    //     invoiceId4forAccount2 <- createInvoce(2000, accountId2, "2020-2-4")
    //     invoiceId5forAccount2 <- createInvoce(20000, accountId2, "2020-2-5")

    //     paymentId1forAccount2 <- payInvoice(invoiceId3forAccount2, "2020-1-4")

    //     balanceForAccount1    <- accountBalance(accountId1)

    //   } yield (accountId1, balanceForAccount1)

    //   for {
    //     results <-  program.interpret[IO][IO]
    //     _       <- IO(println(f"Account ID: ${results._1}"))
    //     _       <- IO(println(f"Balance: ${results._2}"))
    //   } yield ExitCode.Success


  }
}
