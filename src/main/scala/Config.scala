//import org.http4s.rho.swagger.models.Info
//import todo.Configuration.{ApiInfoConfig, DbConfig, HttpServerConfig}

package object Configuration {

  object DbConfig {
    val dbDriver = "org.sqlite.JDBC"
    val dbUrl = "jdbc:sqlite:assignment.db"
  }
}