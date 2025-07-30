package com.example.appstock.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.*

/**
 * Utility class for generating QR codes and barcodes
 */
object QRCodeGenerator {
    
    /**
     * Generate a QR code bitmap for a product
     */
    fun generateQRCode(productId: Long, productName: String, price: String): Bitmap? {
        return try {
            val qrData = "PRODUCT:$productId|NAME:$productName|PRICE:$price"
            generateBitmap(qrData, BarcodeFormat.QR_CODE, 512, 512)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Generate a barcode bitmap
     */
    fun generateBarcode(data: String): Bitmap? {
        return try {
            generateBitmap(data, BarcodeFormat.CODE_128, 600, 200)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Generate a unique product code
     */
    fun generateProductCode(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random().nextInt(9999)
        return "PRD${timestamp}${String.format("%04d", random)}"
    }
    
    /**
     * Generate a unique barcode
     */
    fun generateUniqueBarcode(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random().nextInt(999999)
        return "${timestamp}${String.format("%06d", random)}"
    }
    
    private fun generateBitmap(data: String, format: BarcodeFormat, width: Int, height: Int): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1
            
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(data, format, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            null
        }
    }
}

