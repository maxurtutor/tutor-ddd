package org.maxur.ddd

import org.maxur.ddd.params.ConfigParams
import org.maxur.mserv.core.builder.Kotlin
import org.maxur.mserv.core.builder.hocon
import org.maxur.mserv.core.domain.BaseService
import org.maxur.mserv.core.embedded.WebServer
import org.maxur.mserv.core.service.properties.Properties
import org.slf4j.LoggerFactory

/**
 * Application Launcher
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>12.06.2017</pre>
 */
object Launcher {

    private fun log() = LoggerFactory.getLogger(Launcher::class.java)

    /**
     * Command line entry point. This method kicks off the building of a application  object
     * and executes it.
     *
     * @param args - arguments of command.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        Kotlin.service {
            name = ":name"
            packages += "org.maxur.ddd"
            properties += hocon()
            services += rest {
                afterStart += this@Launcher::afterWebServiceStart
            }
            afterStart += this@Launcher::afterStart
            beforeStop += { _ -> log().info("Microservice is stopped") }
            onError += { exception -> log().error(exception.message, exception) }
        }.start()
    }

    fun afterStart(configParams: ConfigParams, config: Properties, service: BaseService) {
        log().info("Properties Source is '${config.sources.get(0).format}'\n")
        configParams.log()
        log().info("${service.name} is started")
    }

    fun afterWebServiceStart(service: WebServer) {
        log().info("${service.name} is started on ${service.baseUri}\"")
        log().info(service.entries().toString())
    }

}

