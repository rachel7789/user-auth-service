# User Registration & Authentication Service

## Table of Contents
1. [Overview](#overview)
2. [Development Approach](#development-approach)
3. [Use of AI Tools](#use-of-ai-tools)
4. [Key Technical Decisions](#key-technical-decisions)
5. [Additional Notes](#additional-notes)

---

## Overview

This project was developed as part of a home assignment for a **Java Developer** position at **Super-Pharm**.

My primary focus in this project was not to “implement as many features as possible”, but to build a **clean, structured, and readable system**, similar to how I would design a real service in a professional production environment.  
If time allows, I plan to continue developing features that are more significant from a professional and architectural perspective.

---

## Development Approach

The project was developed in **clear, incremental stages**, where each stage was implemented, tested, and stabilized before moving on to the next one:

- Infrastructure
- Registration
- Email verification
- Login
- JWT-based authentication
- Security configuration
- Profile management

I intentionally avoided writing large amounts of code at once or jumping to advanced solutions before establishing a stable and reliable foundation.

Throughout the process, I consistently emphasized:

- Clear separation between **Controller / Service / Security** layers
- Consistent and unified error handling
- Readable, maintainable code that is easy to extend

---

## Use of AI Tools

During development, I used **ChatGPT** as an external working tool.

The choice to use this tool (and not others) was deliberate. My main priority was to maintain **full control over the codebase** and over every technical decision made during development.

The tool was used for:
- Guidance and reasoning
- Validation of approaches
- Refinement and structuring of solutions

It was **not** used for automatic or blind code generation.

Although other tools (such as Gemini) are widely used and highly capable, I chose ChatGPT because it is already well aligned with my personal working style from previous independent projects. Given the limited timeframe of the assignment, I preferred a familiar and reliable tool that allows me to work efficiently while staying fully accountable for the final result.

All architectural, logical, and security-related decisions were reviewed, fully understood, and implemented by me.

---

## Key Technical Decisions

- JWT-based **stateless authentication** (no sessions)
- **Spring Security** with a dedicated JWT authentication filter
- **Swagger / OpenAPI** for API documentation and manual testing
- **H2** database for simplicity and fast execution
- Unified error response format, including security-related errors

---

## Additional Notes

The project fulfills all **base requirements** of the assignment and was designed in a way that allows:

- Easy future extension (roles, refresh tokens, external databases)
- Integration as part of a larger system
- Clear understanding of the codebase by developers who did not write it  
