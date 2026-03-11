# 🏃 JavaStocks - Gestion de stocks pour événements sportifs

[![Java](https://img.shields.io/badge/Java-11-blue.svg)](https://java.com)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791.svg)](https://postgresql.org)
[![Swing](https://img.shields.io/badge/UI-Swing-orange.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)

## 📋 Description

Application de gestion de stocks pour l'association **Web Courses** qui organise des événements sportifs. 
Permet de gérer les articles (boissons, textiles, denrées), les coureurs, les réservations et les réapprovisionnements.

### Fonctionnalités principales
- ✅ Gestion complète des articles (CRUD)
- ✅ Gestion des coureurs et types d'épreuve
- ✅ Réservations avec vérification des stocks
- ✅ Détection des ruptures et mises en attente
- ✅ Demandes de réapprovisionnement
- ✅ Historique et statistiques

---

## 🛠️ Technologies utilisées

| Composant | Technologie |
|-----------|-------------|
| Langage | Java 11 |
| Base de données | PostgreSQL 17 |
| Interface graphique | Swing |
| Build tool | Maven |
| JDBC | PostgreSQL JDBC Driver 42.6.0 |

---

## 📦 Installation

### Prérequis
- Java JDK 11 ou supérieur
- PostgreSQL 17
- Maven (optionnel)

### Relations entre tables

| Table 1 | Relation | Table 2 |
|---------|----------|---------|
| coureur | 1 ── n   | reservation |
| type_epreuve | 1 ── n | reservation |
| reservation | 1 ── n | reservation_article |
| article | 1 ── n | reservation_article |