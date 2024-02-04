package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.IQueryBuilderRoot;
import com.google.common.base.Strings;
import com.guicedee.guicedinjection.pairing.Pair;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface IRootEntity<J extends IRootEntity<J, Q, I>, Q extends IQueryBuilderRoot<Q, J, I>, I extends Serializable> {
    /**
     * Returns the id of the given type in the generic decleration
     *
     * @return Returns the ID
     */
    @NotNull
    I getId();

    /**
     * Returns the id of the given type in the generic decleration
     *
     * @param id
     * @return
     */
    @NotNull
    J setId(I id);

    /**
     * Returns the builder associated with this entity
     *
     * @return The associated builder
     */
    @NotNull
    Q builder();

    /**
     * Any DB Transient Maps
     * <p>
     * Sets any custom properties for this core entity.
     * Dto Read only structure. Not for storage unless mapped as such in a sub-method
     *
     * @return
     */
    @NotNull
    Map<Serializable, Object> getProperties();
    
    default String getTableName()
    {
        Class<?> c = getClass();
        Table t = c.getAnnotation(Table.class);
        String tableName = "";
        if (t != null)
        {
            String catalog = t.catalog();
            if (!catalog.isEmpty())
            {
                tableName += catalog + ".";
            }
            String schema = t.schema();
            if (!schema.isEmpty())
            {
                tableName += schema + ".";
            }
            if (Strings.isNullOrEmpty(t.name()))
            {
                tableName += c.getSimpleName();
            }
            else
            {
                tableName += t.name();
            }
        }
        if (tableName.isEmpty())
        {
            Entity e = c.getAnnotation(Entity.class);
            if (e != null)
            {
                tableName = e.name();
            }
        }
        if (tableName.isEmpty())
        {
            tableName = getClass()
                    .getSimpleName();
        }
        return tableName;
    }
    
    default Pair<String, Object> getIdPair()
    {
        for (Field field : getFields())
        {
            if (field.isAnnotationPresent(Transient.class) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
            {
                continue;
            }
            try
            {
                Id idCol = field.getAnnotation(Id.class);
                EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
                if (idCol != null)
                {
                    field.setAccessible(true);
                    return Pair.of(getColumnName(field), field.get(this));
                }
            }
            catch (IllegalArgumentException | IllegalAccessException ex)
            {
                Logger.getLogger("RunnableStatement")
                        .log(Level.SEVERE, null, ex);
            }
        }
        return Pair.empty();
    }
    
    default List<Field> getFields()
    {
        List<Field> fields = new ArrayList<>();
        Class<?> i = getClass();
        while (i != null)
        {
            Collections.addAll(fields, i.getDeclaredFields());
            i = i.getSuperclass();
        }
        return fields;
    }
    
    default boolean isColumnReadable(Field field)
    {
        JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
        Column col = field.getAnnotation(Column.class);
        Id idCol = field.getAnnotation(Id.class);
        EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        GeneratedValue genVal = field.getAnnotation(GeneratedValue.class);
        if (col == joinCol && joinCol == idCol && idCol == embId
                && joinCol == oneToOne
                && joinCol == oneToMany
                && joinCol == manyToMany
                && joinCol == manyToOne
                && joinCol == genVal
        ) //if everything is null go to next field, easier than is nulls
        {
            return false;
        }
        if (Collection.class.isAssignableFrom(field.getType()))
        {
            return false;
        }
        return true;
    }
    
    default String getColumnName(Field field)
    {
        JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
        Column col = field.getAnnotation(Column.class);
        EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
        String columnName = "";
        
        if (joinCol != null)
        {
            columnName = joinCol.name();
        }
        else if (col != null)
        {
            columnName = col.name();
        }
        else if (embId != null)
        {
            columnName = "";
        }
        
        if (embId != null)
        {
            try
            {
                Object o = field.get(this);
                Field[] f = o.getClass()
                        .getDeclaredFields();
                StringBuilder colNames = new StringBuilder();
                for (Field field1 : f)
                {
                    if (isColumnReadable(field1))
                    {
                        colNames.append(getColumnName(field1))
                                .append(",");
                    }
                }
                colNames.deleteCharAt(colNames.length() - 1);
                return colNames.toString();
            }
            catch (IllegalAccessException e)
            {
                columnName = "";
            }
        }
        if (columnName.isEmpty())
        {
            columnName = field.getName();
        }
        return columnName;
    }
}
