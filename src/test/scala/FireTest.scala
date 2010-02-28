import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FireTest extends FlatSpec with ShouldMatchers {
  "The Internet" should "not be on fire" in {
    "Internet" should not equal ("fire")
  }
}
