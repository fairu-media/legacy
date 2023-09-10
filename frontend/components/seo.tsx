import Head from "next/head";

export type SEOProps = {
    description?: string;
    title?: string;
    type?: string;
};

export default function SEO({
    title       = 'Fairu',
    description = 'A painless image-hosting server.',
    type        = 'website'
}: SEOProps) {
    return (
        <Head>
            <title>{title}</title>
            <meta name="description" content={description} />
            <meta property="og:type" content={type} />
            <meta property="og:description" content={description} />
            <meta property="og:title" content={title} />
            <meta name="twitter:title" content={title} />
            <meta name="twitter:description" content={description} />
        </Head>
    );
}