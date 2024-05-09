import com.augustnagro.magnum.*



/*
https://github.com/AugustNagro/magnum
Treiber: verm. unnötig bei docker
"org.postgresql" % "postgresql" % "<version>"

val ds: java.sql.DataSource = ???
val users: Vector[User] = connect(ds):
  sql"SELECT * FROM user".query[User].run()



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
  case class QueryResult(
	id: Int, 
	url: String, 
	content: String, 
	similarity: Double
  )