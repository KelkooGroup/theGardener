**Retour d'expériences sur une nouvelle manière d'appréhender la documentation technique. Inclure celle ci dans le cycle de développement**, nous a permis à KelkooGroup de rendre cette tache, à priori rébarbative, beaucoup plus attrayante pour les développeurs. Nous avons enfin une documentation technique sur laquelle s'appuyer !

**Un principe, un outil, quelques règles simples et le tour est joué, nous faisons confiance en notre documentation.**

On peut faire le parallèle avec les tests. **A un moment donné les tests étaient chiants,** on ne voyait pas l'intérêt, ça faisant perdre un temps précieux et puis d'abord tester c'était douter.... et il y a eu l’avènement du TDD et **d'un seul coup, les tests sont devenus excitants à écrire par le développeur**, on a trouvé un grand intérêt pour la couverture de test, la non régression et j'en passe....

 **On a un peu le même sentiment avec la documentation, c'est chiant à écrire**, ça n'a pas d'intérêt : mon application qu'elle soit une interface graphique ou une API est suffisamment bien conçue pour pourvoir être utilisée sans documentation....   

Cela pose un problème car en général notre interface n'est pas suffisamment bien conçue pour pourvoir être utilisée sans explications. Nous développons des applications qui sont plus complexes qu'un tire bouchon ! => nous avons besoin de fournir une documentation pertinente et à jour, or **la documentation que l'on produit aujourd'hui est souvent  peu digne de confiance** . 

**La documentation de nos applications est, en général, en décalage avec ce que fait réellement l'application** : à force de réusinage et de changements de direction du produit, la documentation d'origine n'est pas à jour et souvent non revue.

**Je vous propose une idée toute simple : inclure l'écriture de la documentation dans le cycle de développement pour qu'elle puisse être revue lors des merge request / pull request en même temps que le code !** 


## En bref

- In our documentation we trust: 
  - but de la présentation: proposer une nouvelle démarche s'appuyant sur des règles et un outil
  - avant de présenter la démarche, on prendra un peu de temps pour redonner le contexte Kelkoo 
  - découpage de la conférence: 
     - 2/3 discours
     - 1/3 démo
- Presentation de l'orateur DEV / EM / PO de theGardener
  - différents rôles, différents besoin de documentation  
- Contexte Kelkoo
- Documentation en Cycle en V ou WaterFall
- Documentation en mode agile: 
   - aucune ou le bordel ou information erronée => peu de confiance
- Digression, sur les tests
   - 2 mots sur la pratique, mais surtout son mode d'adoption  
- TDD => BDD
   - BDD: TDD gros grain qui permet de définir de la documentation technique exhaustive et executable
- Pourquoi on a du mal à écrire de la documentation ? 
- Pourquoi ne pas inclure l'écriture de cette documentation dans le cycle de DEV ? 
- Documentation Trust Agreement
  - S'engager en tant qu'équipe sur:
     - Quand, Comment, Où, Qui, à Qui: Audience, Quoi: Niveau Platforme, Niveau Projet, Niveau Fonctionnalité
- [theGardener](https://kelkoogroup.github.io/theGardener/): 
   - Projet innovation pour adresser le besoin de générer de la documentation 
   - Fonctionnement général  

- Démo de l'application [theGardener](https://kelkoogroup.github.io/theGardener/)
   - Via theGardener en VPN dans le reseau Kelkoo 
      - Présentation de la Documentation Trust Agreement de notre équipe
      - Présentation de la documentation de la platforme documentée par cette pratique      
      - Présentation d'un projet documenté par cette pratique
      - Ajout de documentation live en utilisant theGardener


## Support
- [Le discours](https://github.com/KelkooGroup/theGardener/blob/master/documentation/assets/decks/InOurDocumentationWeTrust.md.pdf)
- [Les slides](https://github.com/KelkooGroup/theGardener/blob/master/documentation/assets/decks/InOurDocumentationWeTrust.pdf)
   - Presentation basée uniquement sur des photos. Le but est se concentrer sur le discours.
   - Fil rouge des slides est une analogie entre les légumes et la documentation. 
   
## Référence    
- [in-our-documentation-we-trust présenté à SnowCamp le 24/01/2020](https://snowcamp2020.sched.com/event/XoPh/in-our-documentation-we-trust-rex-sur-lintegration-de-lecriture-de-la-doc-dans-le-cycle-de-developpement?iframe=no&w=100%&sidebar=no&bg=no)