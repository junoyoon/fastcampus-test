import static net.grinder.script.Grinder.grinder
import static net.grinder.util.GrinderUtils.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import groovy.json.*
import net.grinder.script.*

import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.ngrinder.http.*
import org.ngrinder.http.cookie.*

@RunWith(GrinderRunner)
class TestRunner {
    static GTest test1, test2
    static HTTPRequest request
    static Map<String, String> headers = [
            "Content-Type":"application/json",
            //"Authorization": "Basic YWRtaW46YWRtaW4="
    ]

//    static Random rnd = new Random()
//    static def ownerIds
    static def host = "127.0.0.1:8080"
//    JsonSlurper slurper = new JsonSlurper()

    @BeforeProcess
    static void beforeProcess() {
        HTTPRequestControl.setConnectionTimeout(300000)
        test1 = new GTest(1, "127.0.0.1")
        test2 = new GTest(2, "127.0.0.2")
        request = new HTTPRequest()
        request.setHeaders(headers)
//        request.GET("http://${host}/api/owners").with {
//            ownerIds = new JsonSlurper().parseText(getBodyText()).collect { it.id }
//        }
        grinder.logger.info("before process.")
    }

    @BeforeThread
    void beforeThread() {
        grinder.logger.info("before thread. ${getThreadUniqId()}")
        test1.record(this, "testGet")
        test2.record(this, "testPost")
        grinder.statistics.delayReports = true
        grinder.logger.info("before thread.")
    }

    @Before
    void before() {
        grinder.logger.info("before. init cookies")
    }

    @RunRate(100) @Test
    void testGet() {
        // def ownerId = ownerIds[rnd.nextInt(this.ownerIds.size())]
        def param = ["hello":"world"]
//        request.GET("http://${host}/api/owners/${}", param) with {
//            assertThat(statusCode, is(200))
//        }
    }

    @RunRate(1) @Test
    void testPost() {
//        def jsonString = JsonOutput.toJson(["firstName": "hello", "lastName": "world", "address": "test", "city": "test", "telephone": "1234"])
//        HTTPResponse response = request.POST("http://${host}/api/owners", jsonString.getBytes())
//        assertThat(response.statusCode, is(201))
//        def jsonBody = slurper.parseText(response.getBodyText())
//        assertThat(jsonBody.id, notNullValue())
    }
}
