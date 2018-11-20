package models

case class Criteria(id: String, hierarchy: Seq[HierarchyNode], projects: Seq[Project] = Seq(), children: Seq[Criteria] = Seq())
