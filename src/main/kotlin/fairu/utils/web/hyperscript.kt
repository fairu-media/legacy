package fairu.utils.web

import kotlinx.html.CommonAttributeGroupFacade

var CommonAttributeGroupFacade.hyperscript: String
    get() = stringAttribute[this, "_"]
    set(value) = stringAttribute.set(this, "_", value)
