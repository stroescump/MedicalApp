package eu.ase.grupa1088.licenta

class User {
    var nume: String? = null
    var nrTel: String? = null
    var email: String? = null
    var parola: String? = null
    var cnp: String? = null

    constructor() {}
    constructor(nume: String?, nrTel: String?, email: String?, parola: String?, cnp: String?) {
        this.nume = nume
        this.nrTel = nrTel
        this.email = email
        this.parola = parola
        this.cnp = cnp
    }
}