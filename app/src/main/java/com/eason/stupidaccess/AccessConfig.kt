package com.eason.stupidaccess

object AccessConfig {
    // 是否已进入登录页
    var HAD_LOGIN = false
    // 是否已进入首页
    var HAD_SHOW_MAIN = false


    // 帮我写一个工具方法，入参为一个函数，以及延迟时间，循环次数，经过入参的延迟时间循环执行入参的函数，循环次数为入参的次数
    fun loopRun(delay: Long, times: Int, block: () -> Unit) {
        for (i in 0 until times) {
            Thread.sleep(delay)
            block()
        }
    }
}