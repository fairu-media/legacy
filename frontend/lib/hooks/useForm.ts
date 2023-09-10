import { useFormik } from "formik";
import { PartialRecord, Promisable } from "lib/types";
import { HTMLInputTypeAttribute } from "react";

export interface Fields {
    [key: string]: any;
}

export type UseFormikReturn<FIELDS extends Fields = Fields> = ReturnType<typeof useFormik<FIELDS>>

export interface Field<FIELDS extends Fields = Fields, FIELD extends keyof FIELDS = keyof FIELDS> {
    defaultValue: FIELDS[FIELD];
    type?: HTMLInputTypeAttribute;
    placeholder?: string;
    validate?: (value: FIELDS[FIELD]) => Promisable<string | undefined>;
    required?: boolean;
}

export type FieldObject<FIELDS extends Fields = Fields> = {
    [KEY in keyof FIELDS]: Field<FIELDS, KEY>
}

export interface Form<FIELDS extends Fields = Fields> {
    fields: FieldObject<FIELDS>;
    formik: UseFormikReturn<FIELDS>;
}

export interface FormFieldOptions<FIELDS extends Fields = Fields, FIELD extends keyof FIELDS = keyof FIELDS> extends Field<FIELDS, FIELD> {
    name: FIELD;
}

export interface FormOptions<FIELDS extends Fields = Fields> {
    fields: FormFieldOptions<FIELDS>[];
    onSubmit: (fields: FIELDS) => void;
}

/** A hook to use formik with an even cringier api. */
export function useForm<FIELDS extends Fields = Fields>(options: FormOptions<FIELDS>): Form<FIELDS> {
    const defaults: Partial<FIELDS> = {}, fields: Partial<FieldObject<FIELDS>> = {}
    for (const field of options.fields) {
        defaults[field.name] = field.defaultValue
        fields[field.name] = field;
    }

    return {
        formik: useFormik<FIELDS>({
            initialValues: defaults as FIELDS,
            onSubmit: options.onSubmit,
            validate: async values => {
                const errors: PartialRecord<keyof FIELDS, string> = {};
                for (const field of options.fields) {
                    const validation = await field.validate?.(values[field.name]);
                    if (!validation) continue;
                    errors[field.name] = validation;
                }
    
                return errors;
            }
        }),
        fields
    } as Form<FIELDS>;
}
