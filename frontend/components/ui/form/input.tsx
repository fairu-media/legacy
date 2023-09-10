import { Label } from "@blueprintjs/core";
import { capitalize } from "lib/common/capitalize";
import { Fields, Form } from "../../../lib/hooks/useForm";

export interface FormInputProps<FIELDS extends Fields> {
    name: keyof FIELDS,
    form: Form<FIELDS>,
}

export function FormInput<FIELDS extends Fields>({ form, name: fieldName }: FormInputProps<FIELDS>) {
    const name  = fieldName as string
        , field = form.fields[name]
        , error = form.formik.errors[name];

    return (
        <div>
            <Label htmlFor={name} className="font-semibold">
                {field.required && <span className={"text-red-300"}>*</span>}
                {capitalize(name)}
            </Label>

            <div className="bp4-input-group">
                <input
                    id={name}
                    name={name}
                    type={field.type}
                    placeholder={field.placeholder}
                    onChange={form.formik.handleChange}
                    value={form.formik.values[fieldName]}
                    className="bp4-input"
                />
            </div>

            {error && <span className={"text-red-300 text-xs lowercase"}>{error as string}</span>}
        </div>
    );
}
