package vinoteka.model

interface ModelWithUser : User {
    var user: UserImpl

    override var name: String
        get() = user.name
        set(value) {
            user.name = value
        }
    override var role: UserRole
        get() = user.role
        set(value) {
            user.role = value
        }

    override var login: String
        get() = user.login
        set(value) {
            user.login = value
        }
    override var password: String
        get() = user.password
        set(value) {
            user.password = value
        }
}
