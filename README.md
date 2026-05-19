# NaturalADA_PoC

Natural/Adabas Code Archive - Legacy mainframe programs for insurance premium processing and accounts reporting.

## Overview

This repository contains legacy Natural/Adabas programs used for:
- Insurance premium rate updates
- Accounts file processing and reporting
- Agent commission calculations

## Files

### 1. Natural Adabas Structured Mode program.txt

**Purpose:** Premium rate update for MO POLS (Base Premium)

**Database:** CENT-CODES-FILE2

**Key Operations:**
- Reads table records for TABLE-TYPE 150
- Updates records with DATE-KEY 99999999 to end date 2015-03-31
- Creates new records with updated premium rates (26667)
- Exports before/after states to work files

**Program Flow:**
```
R1: READ - Export current state to work file 1
R2: READ - Update DATE-9999 records to end date (GET/UPDATE)
R3: READ - Create new records with DATE-9999 and new rates (STORE)
R4: READ - Export updated state to work file 2
```

**Key Variables:**
- `#COV-SUPER` - Search key (TABLE-TYPE + COVERAGE + GROUP + AREA + DATE)
- `DATE-2015-MAR` - End date: 20150331
- `DATE-9999` - Open/Current date indicator: 99999999

---

### 2. Natural Adabas Reporting Mode program.txt

**Purpose:** Accounts file reporting with agent commission processing

**Databases:**
- ACCOUNTS-FILE (by ACC-KEY)
- AGENT-CONTROLS (lookup for agent details)

**Key Operations:**
- Multi-fetch read of accounts (1000 records at a time)
- Agent validation and lookup
- Premium, commission, and cash amount calculations
- Date filtering (ENTRY-DATE > 20131199)
- Method/Process marker filtering

**Key Variables:**
- `#DEB`, `#COMM`, `#CASH` - Amount calculations
- `#DEB-AMT`, `#COMM-AMT`, `#CASH-AMT` - Accumulators
- `#EM-PREM`, `#EM-COMM`, `#EM-CASH` - Edited masks for display
- `#MAIN-AGT-KEY` - Agent lookup key
- `#VALID` - Validation flag

**Business Logic:**
- Skips entries with METHOD-COLL markers 'R', 'X', 'S'
- Validates agent status = 'A' (Active)
- Handles negative amounts with sign indicators
- Formats amounts with edit masks (EM=9999999.99)

## Technology Stack

- **Language:** Natural (Software AG)
- **Database:** ADABAS
- **Operating Environment:** Mainframe (z/OS)
- **Programming Modes:**
  - Structured Mode (DEFINE DATA blocks)
  - Reporting Mode (free-form variable definitions)

## Program Syntax Notes

| Natural Construct | Description |
|-------------------|-------------|
| `DEFINE DATA LOCAL` | Local variable declaration block |
| `VIEW OF` | Database view mapping |
| `REDEFINE` | Variable overlay/redefinition |
| `READ ... BY` | Sequential read by descriptor |
| `FIND ... WITH` | Search by key |
| `GET *ISN` | Fetch record by internal sequence number |
| `UPDATE` | Modify existing record |
| `STORE` | Insert new record |
| `END TRANSACTION` | Commit changes |
| `ESCAPE TOP` | Skip to next loop iteration |
| `ESCAPE BOTTOM` | Exit loop |
| `MOVE EDITED ... TO` | Format with edit mask |
| `COMPRESS ... INTO ... LEAVING NO` | Concatenate without spaces |
| `RESET` | Clear variable to default |

## License

This is legacy code archived for reference purposes.

