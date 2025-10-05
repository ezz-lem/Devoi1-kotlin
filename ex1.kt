// Classe de base Personne

open class Personne(val nom: String, val prenom: String, val email: String) {
    open fun afficherInfos() {
        println("Nom: $nom, Prénom: $prenom, Email: $email")
    }
}

// Classe Utilisateur qui hérite de Personne


class Utilisateur(nom: String, prenom: String, email: String, val idUtilisateur: Int) :
    Personne(nom, prenom, email) {
    val emprunts = mutableListOf<Emprunt>()

    fun emprunterLivre(livre: Livre, dateEmprunt: String) {
        if (livre.disponiblePourEmprunt()) {

            val emprunt = Emprunt(this, livre, dateEmprunt, null)
            emprunts.add(emprunt)
            livre.mettreAJourStock(livre.nombreExemplaires - 1)
            println("$prenom $nom a emprunté le livre '${livre.titre}' le $dateEmprunt")
        } else {
            println("Le livre '${livre.titre}' n’est pas disponible pour l’emprunt.")
        }
    }

    fun afficherEmprunts() {
        if (emprunts.isEmpty()) {
            println("Aucun emprunt pour $prenom $nom")
        } else {
            println("Emprunts de $prenom $nom :")
            emprunts.forEach { it.afficherDetails() }
        }
    }
}

// Classe Livre


class Livre(val titre: String, val auteur: String, val isbn: String, var nombreExemplaires: Int) {
    fun afficherDetails() {
        println("Livre: $titre, Auteur: $auteur, ISBN: $isbn, Exemplaires: $nombreExemplaires")
    }

    fun disponiblePourEmprunt(): Boolean {
        return nombreExemplaires > 0
    }

    fun mettreAJourStock(nouveauStock: Int) {
        nombreExemplaires = nouveauStock
    }
}

// Classe Emprunt


class Emprunt(val utilisateur: Utilisateur, val livre: Livre,
              val dateEmprunt: String, var dateRetour: String?) {
    fun afficherDetails() {
        println("Emprunt: Livre='${livre.titre}'," +
                " Utilisateur=${utilisateur.prenom} ${utilisateur.nom}," +
                " Date emprunt=$dateEmprunt," +
                " Date retour=${dateRetour ?: "Pas encore retourné"}")
    }

    fun retournerLivre(date: String) {
        dateRetour = date
        livre.mettreAJourStock(livre.nombreExemplaires + 1)
        println("Le livre '${livre.titre}' a été retourné le $date")
    }
}

// Classe abstraite GestionBibliotheque


abstract class GestionBibliotheque {
    val utilisateurs = mutableListOf<Utilisateur>()
    val livres = mutableListOf<Livre>()

    abstract fun ajouterUtilisateur(utilisateur: Utilisateur)
    abstract fun ajouterLivre(livre: Livre)
    abstract fun afficherTousLesLivres()
}

// Classe Bibliotheque


class Bibliotheque : GestionBibliotheque() {
    override fun ajouterUtilisateur(utilisateur: Utilisateur) {
        utilisateurs.add(utilisateur)
    }

    override fun ajouterLivre(livre: Livre) {
        livres.add(livre)
    }

    override fun afficherTousLesLivres() {
        println("Liste des livres :")
        livres.forEach { it.afficherDetails() }
    }

    fun rechercherLivreParTitre(titre: String): Livre? {
        return livres.find { it.titre.equals(titre, ignoreCase = true) }
    }
}

// ----------- Programme principal -----------
fun main() {
    // Création de la bibliothèque
    val bibliotheque = Bibliotheque()

    // 1. Créer plusieurs livres
    val livre1 = Livre("PROGRAMMATION ANDROID", "NISRINE", "ISBN001", 3)
    val livre2 = Livre("Programmation Android", "Ahmed", "ISBN002", 2)
    val livre3 = Livre("Programmation AVEC C", "Sara", "ISBN003", 1)

    // 2. Ajouter les livres à la bibliothèque

    bibliotheque.ajouterLivre(livre1)
    bibliotheque.ajouterLivre(livre2)
    bibliotheque.ajouterLivre(livre3)

    // 3. Créer plusieurs utilisateurs
    val user1 = Utilisateur("ZEROUAL", "NISRINE", "nisrine@gmail.com", 1)
    val user2 = Utilisateur("OUCHANNI", "FOUAD", "fouad@gmail.com", 2)

    // Ajouter les utilisateurs à la bibliothèque
    bibliotheque.ajouterUtilisateur(user1)
    bibliotheque.ajouterUtilisateur(user2)


    user1.emprunterLivre(livre1, "2025-10-04")
    user2.emprunterLivre(livre2, "2025-10-04")
    user2.emprunterLivre(livre3, "2025-10-04")

    // Afficher informations

    bibliotheque.afficherTousLesLivres()
    user1.afficherEmprunts()
    user2.afficherEmprunts()

    // 5. Retour d’un livre

    val empruntUser2 = user2.emprunts[0]
    empruntUser2.retournerLivre("2025-10-05")

    // Affichage après retour
    bibliotheque.afficherTousLesLivres()
    user2.afficherEmprunts()

    // Exemple de recherche
    val titreRecherche = "Kotlin pour débutants"
    val livreTrouve = bibliotheque.rechercherLivreParTitre(titreRecherche)

    livreTrouve?.afficherDetails() ?: println("Aucun livre avec le titre '$titreRecherche'")

}
