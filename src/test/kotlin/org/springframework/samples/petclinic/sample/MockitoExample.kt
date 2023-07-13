package org.springframework.samples.petclinic.sample

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.lang.RuntimeException


class MockitoExample {

    @Test
    fun test_mock() {
        // 생성하면서 stub 정의
        val mocked: List<String> = mock { on { get(0) }.doReturn("first") }
        // 별도 stub 추가
        whenever(mocked.get(1)).thenReturn("second")
        // coroutine suspend method 호출
        wheneverBlocking { mocked.get(2) }.thenReturn("third")
        // doReturn 이 먼저 나오는 스타일. get(2) 가 호출되지 않음
        Mockito.doReturn("fourth").whenever(mocked).get(3)
        // answer 를 사용하여 로직 기반 return
        whenever(mocked.get(4)).thenAnswer {
            if (it.arguments[0] == 4) "fifth" else "unknown"
        }
        whenever(mocked.get(5)).thenThrow(RuntimeException())

        assertSoftly(mocked) {
            get(0) shouldBe "first"
            get(1) shouldBe "second"
            get(2) shouldBe "third"
            get(3) shouldBe "fourth"
            get(4) shouldBe "fifth"
            shouldThrow<RuntimeException> { mocked.get(5) }
            get(6) shouldBe null
        }
        // 실행 결과 검증
        verify(mocked).get(0)
        verify(mocked, times(7)).get(any())
    }


    @Test
    fun test_spy() {

        val list = listOf("org-first", "org-second")
        // 생성하면서 stub 정의. 2개 리턴
        val mocked: List<String> = spy(list) {
            // 어떤 int 가 들어오더라도 any 리턴
            on { get(any<Int>()) }.doReturn("any")
            // 추가 stub 정의는 조건을 덮어쓰기
            on { get(0) }.doReturn("first")
            on { get(1) }.doReturn("second")
        }
        assertSoftly(mocked) {
            get(0) shouldBe "first"
            get(1) shouldBe "second"
            get(2) shouldBe "any"
            // 본래 사이즈는 2임.
            size shouldBe 2
        }
        // 실행 결과 검증
        argumentCaptor<Int>().apply {
            verify(mocked, times(3)).get(capture())
            allValues[0] shouldBe 0
            allValues[1] shouldBe 1
            allValues[2] shouldBe 2
        }
    }
}


