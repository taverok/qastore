package com.taverok.qastore.domain

import javax.persistence.*

@Entity
data class AccountAddress(
    var city: String? = null,
    var street: String? = null,
    var house: String? = null,
    var apartment: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToOne(optional = false)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    var account: Account? = null
}
