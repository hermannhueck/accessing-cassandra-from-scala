package munittest

class MySuite extends munit.FunSuite {
  test("meaning of life") {
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
  }
}
