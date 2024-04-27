package npo.kib.odc_demo.model

interface Mapper<in I, out O> {
    fun map(input: I): O
}