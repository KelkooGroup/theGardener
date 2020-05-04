package models

import com.outr.lucene4s._
import com.outr.lucene4s.mapper.Searchable
import com.outr.lucene4s.query.SearchTerm
import org.apache.lucene.document.Field

case class IndexPage(id: Int, name: String, label: String)

trait SearchablePerson extends Searchable[IndexPage] {

  // This is necessary for update and delete to reference the correct document.
  override def idSearchTerms(person: IndexPage): List[SearchTerm] = List(exact(id(IndexPage.id))))

  /*
    Though at compile-time all fields will be generated from the params in `Person`, for code-completion we can define
    an unimplemented method in order to properly reference the field. This will still compile without this definition,
    but most IDEs will complain.
   */
  def id: Field[Int]
}
