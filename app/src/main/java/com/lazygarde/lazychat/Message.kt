package com.lazygarde.lazychat

class Message(val message : String, val sendBy : SentType) {

}

enum class SentType{
    Me,
    Bot
}