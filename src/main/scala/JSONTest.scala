import net.sf.{json => json_lib}
import net.sf.json.util.JSONBuilder
import org.json.simple

import java.io.{ByteArrayOutputStream, OutputStreamWriter}

object JSONTest {
  def time(task: () => Any): Long = {
    val start = System.currentTimeMillis
    task()
    System.currentTimeMillis - start
  }

  def jsonLibStreaming() = {
    val out = new OutputStreamWriter(new ByteArrayOutputStream)
    val builder = new JSONBuilder(out)
    builder.array()
    for (i <- 1 to 1000000) {
      builder.`object`().key(i.toString).value(i).endObject()
    }
    builder.endArray()
    out.flush()
  }

  def jsonLibDump() = {
    val array = new json_lib.JSONArray
    for (i <- 1 to 1000000) {
      val obj = new json_lib.JSONObject
      obj.put(i.toString, i)
      array.add(obj)
    }
    val out = new OutputStreamWriter(new ByteArrayOutputStream)
    array.write(out)
    out.flush()
  }

  def count() = {
    for (_ <- 1 to 1000000) ()
  }

  def jsonSimpleDump() = {
    val array = new simple.JSONArray
    val arrayAsList = 
      array.asInstanceOf[java.util.List[java.util.HashMap[String, Int]]]
    for (i <- 1 to 1000000) {
      val obj = new java.util.HashMap[String, Int]
      obj.put(i.toString, i)
      arrayAsList.add(obj)
    }
    val out = new OutputStreamWriter(new ByteArrayOutputStream)
    array.writeJSONString(out)
    out.flush()
  }

  def jsonSimpleStreaming() = {
    val out = new OutputStreamWriter(new ByteArrayOutputStream)
    out.write("[")
    val m = new java.util.HashMap[String, Int]()
    m.put("1", 1)
    simple.JSONValue.writeJSONString(m, out)
    for (i <- 2 to 1000000) {
      val m = new java.util.HashMap[String, Int]()
      m.put(i.toString, i)
      out.write(",")
      simple.JSONValue.writeJSONString(m, out)
    }
    out.write("]")
    out.flush()
  }

  def handwritten() = {
    val out = new OutputStreamWriter(new ByteArrayOutputStream)
    out.write("[{\"1\":1}")
    for (i <- 2 to 1000000) {
      out.write(",{\"")
      out.write(i.toString)
      out.write("\":")
      out.write(i)
      out.write("}")
    }
    out.write("]")
    out.flush()
  }

  def main(args: Array[String]) {
    val results = 
    List(
      "JSONLib Streaming" -> time(jsonLibStreaming),
      "JSONLib Dump" -> time(jsonLibDump),
      "JSON Simple Streaming" -> time(jsonSimpleStreaming),
      "JSON Simple Dump" -> time(jsonSimpleDump),
      "Hand written" -> time(handwritten),
      "Count to a million (no json)" -> time(count)
    )

    println()
    results.foreach {
      case (msg, time) => println("%s took %1.3fs".format(msg, time / 1000d))
    }
  }
}
