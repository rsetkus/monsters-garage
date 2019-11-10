package lt.setkus.monstersgarage

class MonstersGarageFeature : GarageFeature {

    val monsters = listOf(
        "James P. Sullivan",
        "Mike Wazowski",
        "Randall Boggs",
        "Roz",
        "Celia",
        "Fungus",
        "Henry J. Watermoose III",
        "George Sanderson",
        "Pete \"Claws\" Ward",
        "Bile",
        "Flint"
    )

    override fun getMonsterName(): String {
        val randomIndex = (0 until monsters.size - 1).shuffled().first()
        return monsters[randomIndex]
    }
}