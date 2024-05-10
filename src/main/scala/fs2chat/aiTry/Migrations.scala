package fs2chat.aiTry

import com.augustnagro.magnum.*

object Migrations:

  // ... migrations skipped for brevity

  private def createEmbeddingsTable(using DbCon): Unit =
    sql"""CREATE TABLE IF NOT EXISTS docs_embeddings AS
          SELECT id, url, content, pgml.embed('intfloat/e5-small', 'passage: ' || content)::vector(384) AS embedding
          FROM docs""".update.run()