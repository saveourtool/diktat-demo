/**
 * The main entrypoint of diktat-demo application.
 */

package org.cqfn.diktat.demo

import org.cqfn.diktat.demo.controller.DemoController
import org.cqfn.diktat.demo.views.CodeForm
import org.springframework.core.io.ClassPathResource
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.http.MediaType

private const val SERVER_PORT = 8082

fun main(args: Array<String>) {
    val application = webApplication {
        beans {
            bean<DemoController>()
        }
        webMvc {
            port = System.getenv("PORT")?.toInt() ?: SERVER_PORT
            router {
                @Suppress("GENERIC_VARIABLE_WRONG_DECLARATION")
                val controller = ref<DemoController>()
                GET("/") {
                    ok().contentType(MediaType.TEXT_HTML)
                        .body(ClassPathResource("static/index.html"))
                }
                POST("/demo", contentType(MediaType.APPLICATION_JSON)) {
                    ok().contentType(MediaType.APPLICATION_JSON)
                        .body(controller.checkAndFixCode(it.body(CodeForm::class.java)))
                }
                resources("/**", ClassPathResource("static/"))
            }
            converters {
                resource()
                kotlinSerialization()
            }
        }
    }

    application.run(args)
}
