# AI_CONTEXT.md

# HAVEN Android Application

This document provides the required project context for any AI assistant.

Please read this file before generating or modifying code.

---

# Project Overview

HAVEN is an Android application built with:

- Kotlin
- Jetpack Compose
- Material 3 Expressive

This repository contains ONLY the Android application.

Gameplay is NOT implemented here.

Unity gameplay exists in a separate repository:

Haven-Game

The Android app is the host application.

Unity is only responsible for rendering the forest and future gameplay.

---

# Project Goal

This repository is currently in the Prototype phase.

The objective is:

Validate the product idea.

Development speed is more important than production architecture.

Avoid unnecessary complexity.

---

# Repository Responsibilities

Android is responsible for:

- Home Screen
- Dashboard
- Timer
- Navigation
- Settings
- Notifications
- User Experience
- Future Unity communication

Unity is responsible for:

- Forest
- Trees
- Animals
- Camera
- Isometric world
- Mini games

Never implement gameplay inside the Android project.

---

# Current Status

This project has only been prepared for rapid prototype development.

Current setup includes:

- Kotlin DSL
- Latest stable Kotlin
- Jetpack Compose
- Material 3
- Version Catalog
- Clean package structure

No production features have been implemented.

---

# Folder Structure

app/

core/

    ui/

    common/

    util/

feature/

    home/

    dashboard/

    timer/

    forest/

    profile/

    settings/

navigation/

ui/

    theme/

Please follow this structure.

Do not move folders unless explicitly instructed.

---

# UI / UX Style

HAVEN follows a consistent visual identity.

Design language:

- Material 3 Expressive
- Minimal
- Floating UI
- Premium
- Friendly
- Cozy
- Nature-inspired

Main color:

Forest Green

Secondary colors:

Soft Green

Warm Yellow

Sky Blue

Soft Purple

Green must always remain the dominant color.

Avoid dark themes unless requested.

Avoid sharp corners.

Avoid flat layouts.

Prefer floating cards.

Prefer generous spacing.

Prefer smooth animations.

---

# Design Principles

Every screen should have:

One primary action

Large rounded cards

Large touch targets

Minimal text

Clear hierarchy

Comfortable spacing

Premium appearance

The application should feel peaceful rather than productive.

---

# Coding Principles

Keep the code simple.

Avoid unnecessary abstractions.

Avoid over engineering.

Write readable code.

Do not introduce libraries unless required.

Implement only what has been requested.

Do not redesign existing architecture.

---

# Current Architecture

Android Host

↓

Navigation

↓

Feature Screens

↓

Future Unity Bridge

↓

Unity Gameplay

Unity integration has NOT been implemented yet.

Do not add Unity communication unless requested.

---

# Important

This is a prototype.

Prefer:

Simple

Fast

Maintainable

Readable

over

Enterprise architecture.

If uncertain,

always preserve the existing project structure.

Never redesign the application without explicit instructions.
