package fs2chat.aiTry

import besom.cfg.*

//  https://virtuslab.github.io/besom/docs/intro/

case class Config(
    port: Int,
    openAIApiKey: String,
    jdbcUrl: String,
    docsBaseUrl: String
) derives Configured

@main def main() =
  val config: Config = resolveConfiguration[Config]

object Config:
  def fromEnv[A](key: String, f: String => A = identity): A =
    val strVal =
      try sys.env(key)
      catch
        case _: NoSuchElementException =>
          throw Exception(
            s"Required configuration key $key not present among environment variables"
          )
    try f(strVal)
    catch
      case t: Exception =>
        throw Exception(s"Failed to convert value $strVal for key $key", t)

  val apply : Config =
    new Config(
      fromEnv("PORT", _.toInt),
      fromEnv("OPENAI_API_KEY"),
      fromEnv("JDBC_URL"),
      fromEnv("DOCS_BASE_URL")
    )
    
  val c = """      "model": "azure/gpt4t",
    |      "title": "OpenAI",
    |      "apiBase": "https://litellm.intern-prod.k8s.akdb.net",
    |      "completionOptions": {},
    |      "provider": "openai",
    |      "apiKey": "sk-NqaxJrLlcxxfC5LVCDoQaw",
    |      "requestOptions": {
    |        "caBundlePath": "C:\\Users\\GailH\\AKDB-StammCA.crt"
    |      }
    |""".stripMargin
   
