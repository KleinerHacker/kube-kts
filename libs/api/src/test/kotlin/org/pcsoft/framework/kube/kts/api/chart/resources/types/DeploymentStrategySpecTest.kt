package org.pcsoft.framework.kube.kts.api.chart.resources.types

import org.junit.jupiter.api.Test
import org.pcsoft.framework.kube.kts.api.types.RelativeValue.Companion.absolute
import org.pcsoft.framework.kube.kts.api.types.RelativeValue.Companion.percent
import org.pcsoft.framework.kube.kts.api.utils.toJson
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeploymentStrategySpecTest {

    @Test
    fun testMaxContent() {
        val deploymentStrategySpec = DeploymentStrategySpecBuilder().apply {
            type = DeploymentStrategySpec.Type.RollingUpdate
            rollingUpdate {
                maxSurge = 10.percent
                maxUnavailable = 3.absolute
            }
        }.build()

        assertEquals(DeploymentStrategySpec.Type.RollingUpdate, deploymentStrategySpec.type)
        assertNotNull(deploymentStrategySpec.rollingUpdate)
        assertEquals(10.percent, deploymentStrategySpec.rollingUpdate.maxSurge)
        assertEquals(3.absolute, deploymentStrategySpec.rollingUpdate.maxUnavailable)
    }

    @Test
    fun testMaxContentYaml() {
        val deploymentStrategySpec = DeploymentStrategySpecBuilder().apply {
            type = DeploymentStrategySpec.Type.RollingUpdate
            rollingUpdate {
                maxSurge = 10.percent
                maxUnavailable = 3.absolute
            }
        }.build()

        val actualJson = deploymentStrategySpec.toJson()
        val expectedJson = """{"type":"RollingUpdate","rollingUpdate":{"maxSurge":"10%","maxUnavailable":3}}"""

        println(actualJson)
        println(expectedJson)

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT)
    }

    @Test
    fun testMinContent() {
        val deploymentStrategySpec = DeploymentStrategySpecBuilder().build()

        assertNull(deploymentStrategySpec.type)
        assertNull(deploymentStrategySpec.rollingUpdate)
    }

    @Test
    fun testMinContentYaml() {
        val deploymentStrategySpec = DeploymentStrategySpecBuilder().build()

        assertEquals("""{}""", deploymentStrategySpec.toJson())
    }

    @Test
    fun testRollingUpdateMinContent() {
        val deploymentStrategySpec = DeploymentStrategySpecBuilder().apply {
            rollingUpdate {
            }
        }.build()

        assertNull(deploymentStrategySpec.type)
        assertNotNull(deploymentStrategySpec.rollingUpdate)
        assertNull(deploymentStrategySpec.rollingUpdate.maxSurge)
        assertNull(deploymentStrategySpec.rollingUpdate.maxUnavailable)
    }

    @Test
    fun testRollingUpdateMinContentYaml() {
        val deploymentStrategySpec = DeploymentStrategySpecBuilder().apply {
            rollingUpdate {
            }
        }.build()

        assertEquals("""{"rollingUpdate":{}}""", deploymentStrategySpec.toJson())
    }
}