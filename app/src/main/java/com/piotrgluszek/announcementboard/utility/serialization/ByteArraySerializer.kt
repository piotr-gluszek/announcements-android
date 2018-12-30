package com.piotrgluszek.announcementboard.utility.serialization

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ByteArraySerializer {
    companion object {
        fun serialize(obj: Any): ByteArray {
            lateinit var baos: ByteArrayOutputStream
            lateinit var oos: ObjectOutputStream
            try {
                baos = ByteArrayOutputStream()
                oos = ObjectOutputStream(baos)
                oos.writeObject(obj)
                oos.flush()
                return baos.toByteArray()
            } finally {
                oos.close()
            }
        }

        fun deserialize(byteArray: ByteArray): Any {
            lateinit var bais: ByteArrayInputStream
            lateinit var ois: ObjectInputStream
            try {
                bais = ByteArrayInputStream(byteArray)
                ois = ObjectInputStream(bais)
                return ois.readObject()
            } finally {
                ois.close()
            }
        }
    }
}