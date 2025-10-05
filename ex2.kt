// Main.kt

// --- Exceptions ---
class VehiculeIndisponibleException(message: String) : Exception(message)
class VehiculeNonTrouveException(message: String) : Exception(message)

// --- Partie 1 : classes de base ---

abstract class Vehicule(
    val immatriculation: String,
    val marque: String,
    val modele: String,
    private var _kilometrage: Int,
    private var _disponible: Boolean = true
) {
    val kilometrage: Int
        get() = _kilometrage

    fun estDisponible(): Boolean = _disponible

    fun marquerIndisponible() {
        _disponible = false
    }

    fun marquerDisponible() {
        _disponible = true
    }

    fun mettreAJourKilometrage(km: Int) {
        if (km < _kilometrage) {
            println("Attention: le nouveau KM ($km) est inférieur à ($_kilometrage)")
            return
        }
        _kilometrage = km
    }

    open fun afficherDetails() {
        println("Immatriculation: $immatriculation | Marque: $marque | Modèle: $modele | Kilométrage: $_kilometrage | Disponible: $_disponible")
    }
}

class Voiture(
    immatriculation: String,
    marque: String,
    modele: String,
    kilometrage: Int,
    val nombrePortes: Int,
    val typeCarburant: String,
    disponible: Boolean = true
) : Vehicule(immatriculation, marque, modele, kilometrage, disponible) {

    override fun afficherDetails() {
        println("Voiture -> Immatriculation: $immatriculation |" +
                " $marque $modele | Portes: $nombrePortes | Carburant: $typeCarburant |" +
                " Kilométrage: $kilometrage | Disponible: ${estDisponible()}")
    }
}

class Moto(
    immatriculation: String,
    marque: String,
    modele: String,
    kilometrage: Int,
    val cylindree: Int,
    disponible: Boolean = true
) : Vehicule(immatriculation, marque, modele, kilometrage, disponible) {

    override fun afficherDetails() {
        println("Moto -> Immatriculation: $immatriculation | $marque $modele |" +
                " Cylindrée: ${cylindree}cm³ | Kilométrage: $kilometrage | Disponible: ${estDisponible()}")
    }
}

// --- Partie 2 : conducteurs et réservations ---


class Conducteur(val nom: String, val prenom: String, val numeroPermis: String) {
    fun afficherDetails() {
        println("Conducteur -> $prenom $nom | Permis: $numeroPermis")
    }
}

class Reservation(
    val vehicule: Vehicule,
    val conducteur: Conducteur,
    val dateDebut: String,
    val dateFin: String,
    val kilometrageDebut: Int,
    var kilometrageFin: Int? = null
) {
    private var cloturee: Boolean = false

    init {
        vehicule.marquerIndisponible()
    }

    fun cloturerReservation(kilometrageRetour: Int) {
        if (cloturee) {
            println("Reservation déjà colturee.")
            return
        }
        kilometrageFin = kilometrageRetour
        vehicule.mettreAJourKilometrage(kilometrageRetour)
        vehicule.marquerDisponible()
        cloturee = true
        println("Réservation colturee pour ${vehicule.immatriculation}. Kilométrage au retour: $kilometrageRetour")
    }

    fun estCloturee(): Boolean = cloturee

    fun afficherDetails() {
        println("---- Réservation ----")
        println("Véhicule: ${vehicule.immatriculation} (${vehicule.marque} ${vehicule.modele})")
        println("Conducteur: ${conducteur.prenom} ${conducteur.nom} (Permis: ${conducteur.numeroPermis})")
        println("Période: $dateDebut -> $dateFin")
        println("Kilométrage début: $kilometrageDebut | Kilométrage fin: ${kilometrageFin ?: "non renseigné"}")
        println("Statut: ${if (estCloturee()) "Clôturée" else "En cours"}")
        println("---------------------")
    }
}

// --- Partie 3 : gestion du parc ---

class ParcAutomobile {
    val vehicules: MutableList<Vehicule> = mutableListOf()
    val reservations: MutableList<Reservation> = mutableListOf()

    fun ajouterVehicule(vehicule: Vehicule) {
        vehicules.add(vehicule)
        println("Véhicule ajouté: ${vehicule.immatriculation}")
    }

    fun supprimerVehicule(immatriculation: String) {
        val v = vehicules.find { it.immatriculation == immatriculation }
            ?: throw VehiculeNonTrouveException("Véhicule avec immatriculation $immatriculation non trouvé.")

        val enCours = reservations.any { it.vehicule == v && !it.estCloturee() }
        if (enCours) {
            println("Impossible de supprimer $immatriculation: véhicule avec réservation en cours.")
            return
        }
        vehicules.remove(v)
        println("Véhicule supprimé: $immatriculation")
    }

    fun reserverVehicule(immatriculation: String, conducteur: Conducteur, dateDebut: String, dateFin: String): Reservation {
        val v = vehicules.find { it.immatriculation == immatriculation }
            ?: throw VehiculeNonTrouveException("Véhicule avec immatriculation $immatriculation non trouvé.")
        if (!v.estDisponible()) {
            throw VehiculeIndisponibleException("Le véhicule $immatriculation pas disponible.")
        }
        val res = Reservation(v, conducteur, dateDebut, dateFin, kilometrageDebut = v.kilometrage)
        reservations.add(res)
        println("Réservation créée: véhicule ${v.immatriculation} pour ${conducteur.prenom} ${conducteur.nom}")
        return res
    }

    fun afficherVehiculesDisponibles() {
        println("=== Véhicules disponibles ===")
        val disponibles = vehicules.filter { it.estDisponible() }
        if (disponibles.isEmpty()) {
            println("Aucun véhicule disponible.")
        } else {
            disponibles.forEach { it.afficherDetails() }
        }
    }

    fun afficherReservations() {
        println("=== Toutes les réservations ===")
        if (reservations.isEmpty()) {
            println("Aucune réservation.")
        } else {
            reservations.forEach { it.afficherDetails() }
        }
    }
}

// --- Partie 4 + Démonstration dans main ---
fun main() {
    val parc = ParcAutomobile()
    // 1) Créer un parc automobile avec plusieurs voitures et motos
    val v1 = Voiture("AA-111-BB", "Toyota", "Corolla", 50000, nombrePortes = 4, typeCarburant = "essence")
    val v2 = Voiture("CC-222-DD", "Renault", "Clio", 30000, nombrePortes = 5, typeCarburant = "diesel")
    val v3 = Voiture("EE-333-FF", "Tesla", "Model 3", 15000, nombrePortes = 4, typeCarburant = "électrique")
    val m1 = Moto("GG-444-HH", "Yamaha", "MT-07", 12000, cylindree = 689)
    val m2 = Moto("II-555-JJ", "Honda", "CBR500R", 8000, cylindree = 471)

    parc.ajouterVehicule(v1)
    parc.ajouterVehicule(v2)
    parc.ajouterVehicule(v3)
    parc.ajouterVehicule(m1)
    parc.ajouterVehicule(m2)

    // 2) Ajouter plusieurs conducteurs
    val c1 = Conducteur("BENJAMIN", "Ali", "PERM-12345")
    val c2 = Conducteur("EL HASSAN", "Sara", "PERM-67890")
    val c3 = Conducteur("MARTIN", "Nora", "PERM-54321")

    println()

    // Q-3) Effectuer des réservations et mettre à jour état
    try {
        val r1 = parc.reserverVehicule("AA-111-BB", c1, "2025-10-05", "2025-10-10")
        val r2 = parc.reserverVehicule("GG-444-HH", c2, "2025-10-06", "2025-10-07")
        try {
            parc.reserverVehicule("AA-111-BB", c3, "2025-10-12", "2025-10-15")
        } catch (e: VehiculeIndisponibleException) {
            println("Erreur réservation: ${e.message}")
        }

        r1.cloturerReservation(kilometrageRetour = 50350)

        val r3 = parc.reserverVehicule("AA-111-BB", c3, "2025-10-20", "2025-10-25")

        r2.cloturerReservation(kilometrageRetour = 12100)
        r3.cloturerReservation(kilometrageRetour = 50500)

    } catch (e: VehiculeNonTrouveException) {
        println("Erreur: ${e.message}")
    } catch (e: VehiculeIndisponibleException) {
        println("Erreur: ${e.message}")
    }
    println()


    // QS-4) Gérer exceptions si véhicule n'existe pas
    try {
        parc.reserverVehicule("ZZ-999-XX", c1, "2025-11-01", "2025-11-03")
    } catch (e: VehiculeNonTrouveException) {
        println("Exception attrapée: ${e.message}")
    }

    println()

    // QS-5) Afficher véhicules disponibles et réservations
    parc.afficherVehiculesDisponibles()
    println()
    parc.afficherReservations()

    println()

    // Essayer de supprimer un véhicule
    try {
        parc.supprimerVehicule("CC-222-DD")
        parc.supprimerVehicule("ZZ-000-YY")
    } catch (e: VehiculeNonTrouveException) {
        println("Erreur suppression: ${e.message}")
    }
}
