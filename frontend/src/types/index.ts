// frontend/src/types/index.ts

// Statuts alignés avec le Backend (Enum StatutDemande)
export type StatutDemande = 'DRAFT' | 'SUBMITTED' | 'UNDER_ANALYSIS' | 'VALIDATED' | 'REFUSED' | 'Under_Review' | 'NEED_INFO' | 'APPROVED' | 'PARTIALLY_APPROVED' | 'REJECTED';

// Types de documents
export type TypeDocument = 'BULLETIN' | 'PROGRAMME' | 'MOTIVATION' | 'AUTRE';

// --- Interfaces de base ---

export interface Section {
    code: string;
    label: string; // ou 'name' selon ton DTO backend
}

export interface School {
    code: string;
    etablissement: string;
}

export interface Student {
    id: number;
    email: string;
    prenom: string;
    nom: string;
}

export interface UE {
    code: string;
    libelle: string; // 'name' dans le backend ? à vérifier
    ects: number;
    semester: number;
}

// --- DTOs pour les cours externes ---

export interface ExternalCourse {
    id: number;
    code: string;
    libelle: string; // Match avec 'name' côté Java
    etablissement: string;
    ects: number;
    totalHours?: number;
    urlProgramme?: string;
}

export interface AddExternalCourseDTO {
    etablissement: string;
    code: string;
    libelle: string;
    ects: number;
    urlProgramme?: string;
}

// --- DTOs pour les documents ---

export interface SupportingDocument {
    id: number;
    type: TypeDocument;
    urlStockage: string;
}

export interface AddDocumentDTO {
    type: TypeDocument;
    url: string; // Le backend attend 'urlStockage' ou 'url' ? On adaptera dans l'api.ts
}

// --- DTOs pour les demandes (Exemptions) ---

export interface ExemptionRequestSummary {
    id: string; // Traité comme string pour le front (facilite les substring)
    createdAt: string; // Date ISO
    statut: StatutDemande;
    section: string; // Le code de la section
    studentName?: string;
}

export interface ExemptionRequestFull {
    id: string;
    createdAt: string;
    statut: StatutDemande;
    section: string;
    etudiant: Student;
    externalCourses: ExternalCourse[];
    documents: SupportingDocument[]; // 'globalDocuments' côté Java
    items: any[]; // On verra plus tard pour les détails complexes
}