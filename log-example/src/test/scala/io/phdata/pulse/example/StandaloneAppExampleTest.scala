/* Copyright 2018 phData Inc. */

package io.phdata.pulse.example

import org.scalatest.FunSuite

class StandaloneAppExampleTest extends FunSuite {
  test("Run standalone example") {
    StandaloneAppExample.main(Array("100", "1"))
  }
}
