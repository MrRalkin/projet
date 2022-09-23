package classes.auth

import interfaces.auth.ILogin

class Login(
    override var email: String,
    override var password: String
) : ILogin {
}