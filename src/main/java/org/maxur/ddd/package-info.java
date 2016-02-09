/**
 * ## This packageis root package of T-DDD application.
 *
 * ![Example Diagram](packages.png)
 *
 * @startuml packages.png
 * package DAO
 * package Domain
 * package UI
 * Domain <-- UI
 * DAO <-- Domain
 * @enduml
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>10.02.2016</pre>
 */
package org.maxur.ddd;