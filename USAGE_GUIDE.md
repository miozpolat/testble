# Usage Guide - Simplicity Connect BLE App

## Quick Start Guide

### Prerequisites
- Android device with BLE support
- For WiFi testing: WiFi-enabled Silicon Labs device (Si917/Si915)
- For BLE testing: Silicon Labs BLE development kit
- Network connectivity for WiFi throughput testing

## Core Functionality Overview

### 1. WiFi Throughput Testing

#### Setting Up WiFi Throughput Test
```kotlin
// Example usage in your code:
val viewModel = WifiThroughputViewModel()

// Configure test parameters
val ipAddress = "192.168.1.100"  // Target device IP
val portNumber = 5005            // Test port
val protocol = "TCP"             // TCP, UDP, or TLS

// Start client-side upload test
viewModel.tcpClient(ipAddress, portNumber)

// Start server-side download test  
viewModel.tcpServer(portNumber)
```

#### WiFi Test Types Available:

**TCP Testing:**
- **Upload (TX)**: Mobile sends data to device
- **Download (RX)**: Mobile receives data from device
- **Buffer Size**: 10KB for optimal TCP performance
- **Timeout**: 30 seconds maximum test duration

**UDP Testing:**
- **Upload (TX)**: Mobile sends UDP packets to device
- **Download (RX)**: Mobile receives UDP packets from device  
- **Packet Size**: 1470 bytes (optimized for no fragmentation)
- **Reliability**: Best-effort delivery (no retransmission)

**TLS Testing:**
- **Encrypted**: All data encrypted using TLS 1.2/1.3
- **Certificate**: Built-in certificate management
- **Performance**: Slightly lower than TCP due to encryption overhead

#### Real-time Monitoring
```kotlin
// Observe throughput updates
viewModel.updateSpeed().observe(this) { speedMbps ->
    // Update UI with current speed in Mbps
    speedTextView.text = "${speedMbps} Mbps"
}

// Observe per-second logs
viewModel.updateLogsPerSecond().observe(this) { logs ->
    // Display detailed per-second performance data
    logsAdapter.updateLogs(logs)
}

// Observe final results
viewModel.updateFinalResult().observe(this) { results ->
    val totalTransfer = results["transfer"] // Total bytes transferred
    val avgBandwidth = results["bandwidth"] // Average bandwidth in Mbps
}
```

### 2. BLE Throughput Testing

#### BLE Connection Setup
```kotlin
// In ThroughputActivity
private fun startUploadTest() {
    val service = GattService.ThroughputTestService
    val withNotifications = true // Use notifications for higher throughput
    updateTest = UpdateTest(service, viewModel, withNotifications)
}

private fun stopUploadTest() {
    updateTest?.stopTransmitting()
    updateTest = null
}
```

#### BLE Performance Parameters
```kotlin
// Monitor key BLE parameters that affect throughput:
viewModel.mtuSize.observe(this) { mtu ->
    // MTU size affects packet payload
    // Larger MTU = more data per packet = higher throughput
}

viewModel.connectionInterval.observe(this) { interval ->
    // Connection interval affects how often data can be sent
    // Lower interval = more frequent transmissions = higher throughput
}

viewModel.phyStatus.observe(this) { phy ->
    // PHY type affects maximum throughput
    // 2M PHY provides ~2x throughput vs 1M PHY
}
```

#### BLE Test Configuration
```kotlin
// Throughput test parameters:
- Connection Interval: 7.5ms - 100ms (lower = better throughput)
- MTU Size: 23 - 512 bytes (higher = better efficiency)  
- PHY: 1M, 2M, or Coded PHY (2M provides highest throughput)
- Data Length Extension: Enables larger packets
- Notifications vs Indications: Notifications faster (no ACK required)
```

### 3. Interoperability Testing (IOP)

#### Running Complete IOP Test Suite
```kotlin
// IOPTestActivity automatically runs tests in sequence:
1. Basic Connection Test
2. Service Discovery Test  
3. Throughput Performance Test
4. Security/Bonding Test
5. LE Privacy Test (optional)

// Monitor test progress:
private fun finishItemTest(testId: Int, isRunning: Boolean) {
    when (testId) {
        POSITION_TEST_IOP3_THROUGHPUT -> {
            // Validate throughput meets minimum requirements
            val acceptableThreshold = calculateAcceptableThroughput()
            // Test passes if actual > acceptable
        }
    }
}
```

#### Custom Throughput Thresholds
```kotlin
// Throughput requirements vary by device capability:
private fun calculateAcceptableThroughput(): Int {
    val firmwareVersion = getSiliconLabsTestInfo().firmwareVersion
    val currentPhy = getCurrentPhy()
    
    return when {
        // Modern firmware with 2M PHY
        firmwareVersion >= "6.0" && currentPhy == PHY_LE_2M -> 15000 // 15KB/s
        
        // Standard 1M PHY
        currentPhy == PHY_LE_1M -> 8000 // 8KB/s
        
        // Conservative fallback
        else -> 5000 // 5KB/s
    }
}
```

## Advanced Usage Scenarios

### Custom Test Configuration

#### WiFi Custom Test Setup
```kotlin
class CustomWiFiTest {
    fun runCustomThroughputTest() {
        val viewModel = WifiThroughputViewModel()
        
        // Custom test parameters
        val testDuration = 60000L    // 60 seconds
        val packetSize = 2048        // 2KB packets
        val targetThroughput = 50.0  // 50 Mbps target
        
        // Start test with custom parameters
        // Monitor results and validate against target
    }
}
```

#### BLE Custom Configuration
```kotlin
// Request specific BLE parameters for optimal throughput:
fun requestOptimalBleParams() {
    // Request 2M PHY for maximum throughput
    bluetoothGatt.setPreferredPhy(
        BluetoothDevice.PHY_LE_2M_MASK,
        BluetoothDevice.PHY_LE_2M_MASK,
        BluetoothDevice.PHY_OPTION_NO_PREFERRED
    )
    
    // Request maximum MTU
    bluetoothGatt.requestMtu(517) // 512 bytes payload + 5 byte header
    
    // Request minimum connection interval  
    bluetoothGatt.requestConnectionPriority(
        BluetoothGatt.CONNECTION_PRIORITY_HIGH
    )
}
```

### Error Handling Best Practices

#### WiFi Error Handling
```kotlin
viewModel.handleException().observe(this) { hasException ->
    if (hasException) {
        // Show user-friendly error message
        showErrorDialog("Connection failed. Please check network settings.")
        
        // Log technical details for debugging
        Log.e(TAG, "WiFi throughput test failed: ${getLastError()}")
        
        // Reset test state
        resetTestState()
    }
}
```

#### BLE Error Handling
```kotlin
// Handle BLE-specific errors using ErrorCodes utility:
private fun handleGattError(errorCode: Int) {
    val errorDescription = ErrorCodes.getErrorName(errorCode)
    
    when (errorCode) {
        0x0008 -> { // GATT_INSUF_AUTHORIZATION
            // Request bonding/pairing
            requestBonding()
        }
        0x000F -> { // GATT_INSUF_ENCRYPTION  
            // Enable encryption
            enableEncryption()
        }
        else -> {
            // Generic error handling
            showError("BLE Error: $errorDescription")
        }
    }
}
```

## Performance Optimization Tips

### WiFi Throughput Optimization
1. **Use appropriate buffer sizes**: 10KB for TCP, 1470 bytes for UDP
2. **Monitor network conditions**: Check for interference or congestion
3. **Optimize test duration**: 30-60 seconds for stable measurements
4. **Consider protocol overhead**: TLS has ~10-15% overhead vs TCP

### BLE Throughput Optimization
1. **Request 2M PHY**: Provides ~2x throughput improvement
2. **Maximize MTU**: Request largest supported MTU (up to 512 bytes payload)
3. **Minimize connection interval**: Request high priority connection
4. **Use notifications over indications**: Notifications don't require ACK
5. **Enable Data Length Extension**: Allows larger packets

### General Best Practices
1. **Monitor device temperature**: High throughput can cause thermal throttling
2. **Check battery level**: Low battery may reduce performance
3. **Test in controlled environment**: Minimize RF interference
4. **Use consistent test conditions**: Same distance, orientation, environment

## Troubleshooting Common Issues

### WiFi Connection Issues
- **Connection Refused**: Check if target device is listening on specified port
- **Timeout Errors**: Verify network connectivity and firewall settings
- **Low Throughput**: Check for network congestion or interference

### BLE Connection Issues  
- **Connection Failed**: Ensure device is advertising and in range
- **Service Discovery Failed**: Check GATT database on target device
- **Low Throughput**: Verify optimal BLE parameters (PHY, MTU, interval)

### IOP Test Failures
- **Throughput Test Fails**: Check if device firmware supports required performance
- **Security Test Fails**: Verify bonding/pairing capabilities
- **Discovery Test Fails**: Check GATT service implementation

## API Reference Summary

### Key Classes:
- `WifiThroughputViewModel`: WiFi testing logic
- `ThroughputViewModel`: BLE testing logic  
- `ThroughputUtils`: WiFi utility functions
- `IOPTestActivity`: Interoperability testing
- `ErrorCodes`: Error code mapping

### Key Methods:
- `tcpClient()`: TCP upload testing
- `udpServer()`: UDP download testing
- `startTLSServer()`: TLS testing
- `updateDownload()`: BLE characteristic handling
- `calculateAcceptableThroughput()`: Dynamic threshold calculation

This guide provides comprehensive coverage of the app's testing capabilities and can be used as a reference for both end-users and developers working with the Simplicity Connect codebase.