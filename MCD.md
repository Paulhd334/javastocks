┌─────────────────┐       ┌──────────────────────┐       ┌─────────────────┐
│   fournisseur   │       │ ligne_reapprovision- │       │    article      │
│                 │       │       nement         │       │                 │
│ id (PK)         │◄─────►│ id_fournisseur?      │       │ id (PK)         │
│ nom             │       │ id_article (FK)      │──────►│ libelle         │
│ rue             │       │ id_reappro (FK)      │       │ categorie       │
│ ...             │       │ quantite             │       │ quantite        │
└─────────────────┘       └──────────────────────┘       │ poids/volume    │
         │                                                 └─────────────────┘
         │                                                         ▲
         │         ┌──────────────────────┐                        │
         │         │   reapprovisionnement │                       │
         │         │                       │                       │
         └────────►│ id (PK)               │                       │
                   │ id_fournisseur (FK)   │───────────────────────┘
                   │ id_point_livraison(FK)│
                   │ numero_commande       │
                   │ statut, motif         │
                   └──────────────────────┘
                           │
                           │
                   ┌───────▼────────┐      ┌─────────────────────┐
                   │ point_livraison│      │   ligne_reservation │
                   │                │      │                     │
                   │ id (PK)        │      │ id_article (FK)    │────► article
                   │ nom            │      │ id_reservation (FK)│
                   │ rue, ville     │      │ quantite           │
                   │ telephone      │      └─────────────────────┘
                   └────────────────┘                │
                                                      │
                   ┌─────────────────┐               │
                   │   reservation   │               │
                   │                 │◄──────────────┘
                   │ id (PK)         │
                   │ id_coureur (FK) │─────┐
                   │ id_type_epreuve │──┐  │
                   │ date_reservation│  │  │
                   │ validee/annulee │  │  │
                   └─────────────────┘  │  │
                                        │  │
                   ┌─────────────────┐  │  │
                   │    coureur      │◄─┘  │
                   │                 │     │
                   │ id (PK)         │     │
                   │ nom, prenom     │     │
                   │ date_inscription│     │
                   └─────────────────┘     │
                                           │
                   ┌─────────────────┐     │
                   │ type_epreuve    │◄────┘
                   │                 │
                   │ id (PK)         │
                   │ libelle         │
                   └─────────────────┘