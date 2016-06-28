package net.techcable.tacospigot;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import javax.annotation.Nonnull;

import com.google.common.collect.AbstractIterator;

import static com.google.common.base.Preconditions.*;

@SuppressWarnings("unchecked")
public final class LookupTableMap<V> extends AbstractInt2ObjectMap<V> {
   private static final int DEFAULT_EXPANSION_SIZE = 15;
   private Object[] values;
   private int size;
   private ObjectSet<Int2ObjectMap.Entry<V>> entrySet;
   private ObjectCollection<V> valuesCollection;

   @Override
   public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
       return entrySet != null ? entrySet : (entrySet = new AbstractObjectSet<Int2ObjectMap.Entry<V>>() {
           @Override
           @Nonnull
           public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
               return new EntryIterator<Int2ObjectMap.Entry<V>>() {
                   @Override
                   public Int2ObjectMap.Entry<V> next() {
                       return nextEntry();
                   }
               };
           }

           @Override
           public int size() {
               return size;
           }
       });
   }

   @Override
   @Nonnull
   public ObjectCollection<V> values() {
       return valuesCollection != null ? valuesCollection : (valuesCollection = new AbstractObjectCollection<V>() {

           @Override
           @Nonnull
           public ObjectIterator<V> iterator() {
               return new EntryIterator<V>() {
                   @Override
                   @Nonnull
                   public V next() {
                       return nextEntry().getValue();
                   }
               };
           }

           @Override
           public int size() {
               return size;
           }
       });
   }

   @Override
   @SuppressWarnings("unchecked")
   public V get(int i) {
       Object[] values = this.values;
       return i >= 0 && i < values.length ? (V) values[i] : null;
   }

   @Override
   public V put(int key, V value) {
       checkNotNull(value, "Null value");
       if (key < 0) {
           throw new IllegalArgumentException(negativeKey(key));
       } else if (key >= values.length) {
           expandAndPut(key, value); // Put in a separate method for inlining (its a unlikely slow-case)
           return null;
       } else {
           V oldValue = (V) values[key];
           values[key] = value;
           if (oldValue == null) size++; // New entry
           return oldValue;
       }
   }

   private void expandAndPut(int key, V value) {
       values = Arrays.copyOf(values, key + DEFAULT_EXPANSION_SIZE);
       values[key] = value;
       size++; // Since we've expanded, there was obviously nothing in there before
   }

   @Override
   public V remove(int key) {
       if (key < 0) {
           throw new IllegalArgumentException(negativeKey(key));
       } else if (key >= values.length) {
           return null;
       } else {
           V oldValue = (V) values[key];
           values[key] = null;
           if (oldValue != null) size--; // Entry was there before, but now we're removing it
           return oldValue;
       }
   }

   @Override
   public boolean containsKey(int i) {
       Object[] values = this.values;
       return i >= 0 && i < values.length && values[i] != null;
   }

   @Override
   public void forEach(BiConsumer<? super Integer, ? super V> action) {
       forEachPrimitive((value, key) -> action.accept(key, value));
   }

   public void forEachPrimitive(ObjIntConsumer<V> action) {
       for (int index = 0; index < values.length; index++) {
           V value = (V) values[index];
           if (value != null) {
               action.accept(value, index);
           }
       }
   }

   private String negativeKey(int key) {
       return "Can't add a negative key " + key + " to a LookupTableMap!";
   }

   @Override
   public int size() {
       return size;
   }

   private abstract class EntryIterator<T> implements ObjectIterator<T> {
       private int index = 0;

       @Override
       public int skip(int toSkip) {
           if (toSkip > values.length) toSkip = values.length;
           index += toSkip;
           return toSkip;
       }

       @Override
       public boolean hasNext() {
           while (index < values.length) {
               V value = (V) values[index];
               if (value != null) {
                   return true;
               } else {
                   index++;
               }
           }
           return false;
       }

       /* default */ Int2ObjectMap.Entry<V> nextEntry() {
           while (index < values.length) {
               int key = index++;
               V value = (V) values[key];
               if (value != null) {
                   return new Entry(key, value);
               }
           }
           throw new NoSuchElementException();
       }
   }

   public class Entry implements Int2ObjectMap.Entry<V> {
       private final int key;
       private V value;

       public Entry(int key, V value) {
           this.key = key;
           this.value = checkNotNull(value, "Null value");
       }

       @Override
       public int getIntKey() {
           return key;
       }

       @Override
       public Integer getKey() {
           return getIntKey();
       }

       @Override
       public V getValue() {
           return value;
       }

       @Override
       public V setValue(V value) {
           return put(key, this.value = checkNotNull(value, "Null value"));
       }

   }
}