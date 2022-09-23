package classes.auth

import interfaces.auth.IRegister

class Register(
    override var name: String,
    override var email: String,
    override var password: String
) :IRegister {
}