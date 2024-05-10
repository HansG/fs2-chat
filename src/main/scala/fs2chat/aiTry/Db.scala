package fs2chat.aiTry

import com.augustnagro.magnum.*



/*
https://github.com/AugustNagro/magnum
"com.augustnagro" %% "magnum" % "1.1.1"
Treiber: verm. unnötig bei docker
"org.postgresql" % "postgresql" % "<version>"

val ds: java.sql.DataSource = ???
val users: Vector[User] = connect(ds):
  sql"SELECT * FROM user".query[User].run()

https://postgresml.org/deployments/6dab4000-b3c6-4786-ae31-7144d2025c70
postgres://u_ak85ofcmxxmvayf:choyu8nzxoukxxy@02f7e6f1-1adb-4347-835a-02c74fcccb0e.db.cloud.postgresml.org:6432/pgml_ik1ssi2v1qrejda
*/


class Db(private val ds: javax.sql.DataSource):

  def queryEmbeddings(query: String): Option[Db.QueryResult] =
    connect(ds) {
      sql"""WITH request AS (
              SELECT pgml.embed(
                'intfloat/e5-small',
                'query: ' || $query
              )::vector(384) AS query_embedding
            )
            SELECT
              id,
              url,
              content,
              1 - (
                embedding::vector <=> (SELECT query_embedding FROM request)
              ) AS cosine_similarity
            FROM docs_embeddings
            ORDER BY cosine_similarity DESC
            LIMIT 1""".query[Db.QueryResult].run().headOption
    }
    
object Db:
  case class QueryResult(id: Int, url: String, content: String, similarity: Double)