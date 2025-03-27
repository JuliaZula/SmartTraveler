# Rapport sur l'algorithme du projet Smart Traveler

## 1. Problématique

Dans ce projet, je cherche à résoudre un problème du voyageur de commerce asymétrique (ATSP - Asymmetric Traveling Salesman Problem), qui consiste à trouver le meilleur itinéraire pour visiter plusieurs villes dans une période donnée, tout en minimisant le coût total des billets d'avion et en revenant au point de départ.

Le problème vient du fait que les prix des billets varient en fonction du jour. L'ordre de visite des villes influence donc les dates des vols et, par conséquent, leur coût. Pour trouver une solution efficace, je dois prendre en compte ces variations de prix.

## 2. Analyse du problème

Pour gérer cette variabilité, j’ai choisi une première approche :

- Calculer le coût moyen des vols entre chaque paire de villes sur la période concernée.
- Transformer ensuite le problème en un ATSP classique, qui peut être résolu avec un algorithme d’optimisation.

Mais cette méthode a deux limites :

1. Elle ne prend pas en compte les variations journalières des prix, ce qui peut conduire à une solution qui n’est pas vraiment la moins chère.
2. L’algorithme ATSP va chercher à optimiser l’itinéraire en fonction des coûts moyens, alors que dans la réalité, un vol moins cher peut être disponible à une date précise.

## 3. Solution actuelle

### Approche adoptée :

1. **Traitement des données** : Récupération des prix journaliers les plus bas, puis calcul de la moyenne.  
2. **Modélisation du problème** : Conversion de chaque ville en un nœud ATSP.  
3. **Optimisation avec jsprit** : Utilisation d’une méta (algorithme génétique, recuit simulé, etc.) pour trouver une solution approximative.

### Initialisation des véhicules

```java
private void prepareVehicle() {
    this.vehicleType = VehicleTypeImpl.Builder.newInstance("vehicleType")
            .setCostPerDistance(1.0)
            .build();
    this.vehicle = VehicleImpl.Builder.newInstance("vehicle")
            .setStartLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(getDeparture()))
            .setType(vehicleType)
            .build();
    this.vrpBuilder = VehicleRoutingProblem.Builder.newInstance()
            .setRoutingCost(costMatrix)
            .addVehicle(vehicle);

    for (int i = 1; i < airportsNum; i++) {
        Log.d("Service", "Ajout du service : " + airports[i]);
        Service service = Service.Builder.newInstance(airports[i])
                .setLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(airports[i]))
                .build();
        this.vrpBuilder.addJob(service);
    }
}
```

### Calcul de l’itinéraire optimal

```java
public void calculate() {
    VehicleRoutingProblem problem = vrpBuilder.build();
    VehicleRoutingAlgorithm algorithm = createAlgorithm(problem);
    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

    Log.d("Result","Coût minimum : " + bestSolution.getCost());
    if (bestSolution.getRoutes() == null) {
        Log.d("NULL","Résultat vide");
        throw new RuntimeException("Résultat vide dans RouteCalculator.calculate");
    }

    visitSuite[visitSuiteCursor++] = getDeparture();

    List<VehicleRoute> list = new ArrayList<>(bestSolution.getRoutes());
    list.sort(new com.graphhopper.jsprit.core.util.VehicleIndexComparator());

    for (VehicleRoute route : list) {
        double costs = 0;
        TourActivity prevAct = route.getStart();
        for (TourActivity act : route.getActivities()) {
            String jobId = (act instanceof TourActivity.JobActivity) ? ((TourActivity.JobActivity) act).getJob().getId() : "-";
            double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(), prevAct.getEndTime(), route.getDriver(), route.getVehicle());
            c += problem.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(), route.getVehicle());
            costs += c;
            visitSuite[visitSuiteCursor++] = jobId;
            prevAct = act;
        }
        approximateCost = Math.round(costs);
    }
}
```

## 4. Améliorations futures

1. **Passer à un modèle TD-ATSP (Time-Dependent ATSP)** : au lieu d’utiliser des coûts moyens, prendre en compte la variabilité des prix à chaque étape.
2. **Utiliser OR-Tools** : cette bibliothèque permettrait de trouver des solutions plus proches de l’optimum en intégrant des techniques avancées de programmation linéaire et de recherche opérationnelle.
