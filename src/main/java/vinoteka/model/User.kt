package vinoteka.model

interface User {
    var name: String
    var login: String
    var password: String
    var role: UserRole
}
