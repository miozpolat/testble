# Architecture Diagram - Simplicity Connect BLE App

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Simplicity Connect App                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                              UI Layer (Activities/Fragments)               │
├─────────────────────────────────────────────────────────────────────────────┤
│                              ViewModel Layer (MVVM)                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                              Business Logic Layer                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                              Data/Network Layer                            │
└─────────────────────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   BLE Devices   │    │  WiFi Devices   │    │  Test Results   │
│   (Si Labs)     │    │  (Si917/Si915)  │    │   Storage       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Component Breakdown

### 1. WiFi Throughput Testing Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    WiFi Throughput Module                        │
├──────────────────────────────────────────────────────────────────┤
│  WifiThroughputActivity                                          │
│  ├── User Interface                                              │
│  ├── Test Configuration                                          │
│  └── Results Display                                             │
├──────────────────────────────────────────────────────────────────┤
│  WifiThroughputViewModel                                         │
│  ├── tcpClient()     ┌─────────────────┐                        │
│  ├── tcpServer()     │   Real-time     │                        │
│  ├── udpClient()  ←──┤   Monitoring    │                        │
│  ├── udpServer()     │   (Timer-based) │                        │
│  └── startTLSServer()└─────────────────┘                        │
├──────────────────────────────────────────────────────────────────┤
│  ThroughputUtils                                                 │
│  ├── sendEvent()     ┌─────────────────┐                        │
│  ├── receiUDP()   ←──┤   Network       │                        │
│  └── receiveUDPData() │   Operations    │                        │
│                      └─────────────────┘                        │
├──────────────────────────────────────────────────────────────────┤
│  Network Layer                                                   │
│  ├── TCP Sockets                                                │
│  ├── UDP Sockets                                                │
│  └── TLS/SSL Context                                            │
└──────────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────┐
│        WiFi Device              │
│    (Si917/Si915 Dev Kit)        │
│  ┌─────────────────────────────┐ │
│  │     TCP/UDP/TLS Server      │ │
│  │     Port: 5005 (default)    │ │
│  └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### 2. BLE Throughput Testing Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    BLE Throughput Module                         │
├──────────────────────────────────────────────────────────────────┤
│  ThroughputActivity                                              │
│  ├── GATT Processor      ┌─────────────────┐                    │
│  ├── Command Queue    ←──┤   BLE Stack     │                    │
│  └── Callback Handler    │   Integration   │                    │
│                          └─────────────────┘                    │
├──────────────────────────────────────────────────────────────────┤
│  ThroughputViewModel                                             │
│  ├── updateDownload()    ┌─────────────────┐                    │
│  ├── addBitsToCount() ←──┤   Performance   │                    │
│  ├── toggleTestState()   │   Monitoring    │                    │
│  └── Parameter Tracking  └─────────────────┘                    │
├──────────────────────────────────────────────────────────────────┤
│  UpdateTest (Upload)                                             │
│  ├── Data Generation                                            │
│  ├── Packet Transmission                                        │
│  └── Rate Control                                               │
├──────────────────────────────────────────────────────────────────┤
│  Android BLE Stack                                              │
│  ├── BluetoothGatt                                              │
│  ├── GattCallback                                               │
│  └── Characteristic Operations                                  │
└──────────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────┐
│         BLE Device              │
│    (Silicon Labs Dev Kit)       │
│  ┌─────────────────────────────┐ │
│  │   Throughput Test Service   │ │
│  │ ┌─────────────────────────┐ │ │
│  │ │ TX Characteristic       │ │ │
│  │ │ RX Characteristic       │ │ │
│  │ │ Control Characteristic  │ │ │
│  │ └─────────────────────────┘ │ │
│  └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### 3. Interoperability Testing Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                 Interoperability Test Module                    │
├──────────────────────────────────────────────────────────────────┤
│  IOPTestActivity                                                 │
│  ├── Test Sequence Manager                                      │
│  ├── Result Collector                                           │
│  └── Progress Tracking                                          │
├──────────────────────────────────────────────────────────────────┤
│  Test Cases                                                      │
│  ├── Connection Test         ┌─────────────────┐                │
│  ├── Service Discovery    ←──┤   Test Engine   │                │
│  ├── Throughput Test         │   Automation    │                │
│  ├── Security Test           └─────────────────┘                │
│  └── LE Privacy Test                                            │
├──────────────────────────────────────────────────────────────────┤
│  ItemTestCaseInfo                                               │
│  ├── Status Tracking                                            │
│  ├── Timing Analysis                                            │
│  └── Result Validation                                          │
├──────────────────────────────────────────────────────────────────┤
│  ErrorCodes                                                      │
│  ├── GATT Error Mapping                                         │
│  ├── Debug Information                                          │
│  └── User-Friendly Messages                                     │
└──────────────────────────────────────────────────────────────────┘
```

## Data Flow Diagrams

### WiFi Throughput Test Flow

```
[User Starts Test]
        │
        ▼
[Configure Parameters]
   (IP, Port, Protocol)
        │
        ▼
[Create Network Connection]
        │
        ├─── TCP ──→ [Socket Connection]
        ├─── UDP ──→ [Datagram Socket]
        └─── TLS ──→ [SSL Socket]
        │
        ▼
[Start Timer & Data Transfer]
        │
        ▼
[Real-time Monitoring]
   (Every 1 second)
        │
        ├─── [Update UI]
        ├─── [Calculate Throughput]
        └─── [Log Performance]
        │
        ▼
[Test Completion]
        │
        ├─── [Final Calculations]
        ├─── [Display Results]
        └─── [Cleanup Resources]
```

### BLE Throughput Test Flow

```
[Device Connection]
        │
        ▼
[Service Discovery]
        │
        ▼
[Read BLE Parameters]
   (MTU, Interval, PHY)
        │
        ▼
[Configure Test Settings]
        │
        ▼
[Start Data Transfer]
        │
        ├─── Upload ──→ [Write to Characteristic]
        └─── Download ─→ [Enable Notifications]
        │
        ▼
[Monitor Performance]
   (Characteristic callbacks)
        │
        ▼
[Calculate Throughput]
   (Bits per second)
        │
        ▼
[Display Results]
```

### Interoperability Test Flow

```
[Start IOP Test Suite]
        │
        ▼
[Test 1: Basic Connection]
        │
        ├─── Pass ──→ [Continue to Test 2]
        └─── Fail ──→ [Record Failure & Stop]
        │
        ▼
[Test 2: Service Discovery]
        │
        ├─── Pass ──→ [Continue to Test 3]
        └─── Fail ──→ [Record Failure & Stop]
        │
        ▼
[Test 3: Throughput Performance]
        │
        ├─── Pass ──→ [Continue to Test 4]
        └─── Fail ──→ [Record Failure & Continue]
        │
        ▼
[Test 4: Security/Bonding]
        │
        ├─── Pass ──→ [Continue to Test 5]
        └─── Fail ──→ [Record Failure & Continue]
        │
        ▼
[Test 5: LE Privacy (Optional)]
        │
        ▼
[Generate Test Report]
   (Pass/Fail for each test)
```

## Key Design Patterns

### 1. MVVM Pattern Implementation
```
View (Activity/Fragment)
    ├── Observes LiveData
    ├── Handles User Input  
    └── Updates UI

ViewModel
    ├── Business Logic
    ├── Data Management
    └── LiveData Providers

Model (Utils/Services)
    ├── Network Operations
    ├── BLE Communications
    └── Data Processing
```

### 2. Observer Pattern (LiveData)
```
ViewModel                    Activity
    │                          │
    ├─── _updateSpeed ────────→ speedObserver
    ├─── _perSecondLog ───────→ logObserver  
    ├─── _finalResults ───────→ resultObserver
    └─── _handleException ────→ errorObserver
```

### 3. Command Pattern (BLE Operations)
```
GattProcessor
    │
    ├─── Command Queue
    │    ├── ReadCommand
    │    ├── WriteCommand
    │    └── NotifyCommand
    │
    └─── Sequential Execution
         (Thread-safe with locks)
```

### 4. Strategy Pattern (Protocol Selection)
```
ThroughputTest
    │
    ├─── TCPStrategy
    ├─── UDPStrategy
    └─── TLSStrategy
    
Each strategy implements:
    - connect()
    - transferData()
    - calculateThroughput()
    - cleanup()
```

## Component Interactions

### Real-time Monitoring System
```
Timer (1-second intervals)
    │
    ▼
PeriodicSpeedUpdate
    │
    ├─── Calculate Current Speed
    ├─── Update UI (Main Thread)
    ├─── Log Performance Data
    └─── Reset Counters
    │
    ▼
LiveData Updates
    │
    ▼
UI Refresh
```

### Error Handling Flow
```
Network/BLE Operation
    │
    ├─── Success ──→ [Continue Processing]
    │
    └─── Exception ──→ [Error Handler]
                           │
                           ├─── Log Error Details
                           ├─── Update UI with Error
                           ├─── Cleanup Resources
                           └─── Reset Test State
```

This architecture demonstrates a well-structured Android application following modern development patterns with comprehensive testing capabilities for both WiFi and BLE protocols.