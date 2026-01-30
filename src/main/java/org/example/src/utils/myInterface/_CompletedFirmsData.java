package org.example.src.utils.myInterface;

import lombok.Getter;
import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.byPage.*;

@Getter
public class _CompletedFirmsData {

    public final static Site[] byPage = {
            /* Firms to avoid
            new AddleshawGoddardLLP(), new Andersen(), new ArnoldAndPorter(), new Ashurst(), new CliffordChance(),
            new CovingtonAndBurlingLLP(), new CrowellAndMoring(), new DavisPolkAndWardwell(), new DebevoiseAndPlimpton(), new DechertLLP(),
            new GorrissenFederspiel(), new GreenbergTraurig(), new HerbertSmithFreehillsKramer(), new JonesDay(), new KromannReumert(),
            new LathamAndWatkins(), new Milbank(), new MorganLewis(), new NautaDutilh(), new ProskauerRose(),
            new RopesAndGray(), new Skadden(), new StephensonHarwood(), new Stibbe(), new TaylorWessing(),
            new WhiteAndCase(), new Willkie(),
            */

            // Africa
            new Adsero(), new ALPNGAndCo(), new AlukoAndOyebode(), new AmanAndPartners(), new Ashitiva(),
            new BentsiEnchillLetsaAndAnkomah(), new FisherQuarmbyAndPfeifer(), new KantorAndImmerman(), new ShahidLaw(), new STBB(),
            new TheartMey(),

            // Asia
            new ABNR(), new Aequitas(), new AllBrightLaw(), new AllenAndGledhill(), new ALMTLegal(),
            new AMTLaw(), new AnandAndAnand(), new AquinasLawAlliance(), new AronTadmorLevy(), new AssegafHamzahAndPartners(),
            new BaeKimAndLee(), new BraunPartners(), new BSALaw(), new CFNLaw(), new Deacons(),
            new DRAndAJU(), new FironLaw(), new FoxAndMandal(), new GrandwayLaw(), new Helmsman(),
            new HFAndCo(), new HowseWilliams(), new K1Chamber(), new KECOLegal(), new LeeAndKo(),
            new MASLaw(), new MeitarLaw(), new MorogluArseven(), new MZMLegal(), new OldhamLiAndNie(),
            new Paksoy(), new RamdasAndWong(), new SEUM(), new SFKSLaw(), new SHorowitzAndCo(),
            new ShinAndKim(), new SteinmetzHaringGurman(), new TiruchelvamAssociates(), new VeritasLegal(), new ZhongziLaw(),

            // Europe
            new Aera(), new AGPAdvokater(), new Alliotts(), new AlstonAndBirdLLP(), new Amorys(),
            new AraozAndRueda(), new ArnesenIP(), new ArnoldAndSiedsma(), new ASCHukuk(), new BAHR(),
            new BancilaDiaconuSiAsociatii(), new BARDEHLEPAGENBERG(), new BARENTSKRANS(), new Berggren(), new BlakeMorgan(),
            new BlandyAndBlandy(), new BonelliErede(), new BoodleHatfield(), new BrinkmannAndPartner(), new Broseta(),
            new BrownRudnick(), new BSJP(), new BullAndCo(), new BUREN(), new BureauPlattner(),
            new BussMurtonLaw(), new BYRO(), new ByrneWallace(), new CampbellsLegal(), new CastrenAndSnellman(),
            new CBA(), new Cirio(), new Clarkslegal(), new ClemensLaw(), new CLPLaw(),
            new Codex(), new CollasCrill(), new Contrast(), new CWAAssociates(), new DahlLaw(),
            new DavisPolkAndWardwell(), new DeClercq(), new DechertLLP(), new Delcade(), new DimitrovPetrovAndCo(),
            new DinovaRusevAndPartners(), new DMSLegal(), new Dompatent(), new DrzewieckiTomaszek(),
            new DZPLaw(), new EldibAdvocates(), new Elverdam(), new Esche(), new Eubelius(),
            new Fidal(), new FilipAndCompany(), new Finreg360(), new FIVERS(), new FlichyGrange(),
            new FluegelPreissner(), new Foyen(), new FranklinLaw(), new GanadoAdvocates(), new GorrissenFederspiel(),
            new GPK(), new GrataInternational(), new GreenHorseLegal(), new GVZH(), new Haavind(),
            new HabrakenRutten(), new HammarskioldAndCo(), new HannesSnellman(), new HarperJamesSolicitors(), new Hayes(),
            new HCRLegal(), new Hellstrom(), new Hellstrom(), new HjulmandCaptain(), new Holst(),
            new HPPAttorneys(), new Hugel(), new Jalsovszky(), new Jalsovszky(), new JGSA(),
            new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new Kallan(), new Kanter(), new KewLaw(),
            new KienhuisLegal(), new Knijff(), new Knijff(), new KnezovicAndAssociates(), new KochanskiAndPartners(),
            new Kolster(), new Krogerus(), new KromannReumert(), new LaszczukAndWspolnicy(), new LemstraVanDerKorst(),
            new LEXIA(), new LEXLogmannsstofa(), new Liedekerke(), new Logos(), new LPAGGV(),
            new Lydian(), new MAQS(), new Matheson(), new MazantiAndersen(), new MccannFitzGerald(),
            new McDermottWillAndEmery(), new McDermottWillAndEmery(), new MellingVoitishkinAndPartners(), new MerilampiAttorneys(), new MitelAndAsociatii(),
            new MoalemWeitemeyer(), new Molinari(), new MooreLegalKovacs(), new MSP(), new MVJMarkovicVukoticJovkovic(),
            new MVVPAdvocaten(), new NielsenNorager(), new Njord(), new NOEWE(), new Norens(),
            new NovaLaw(), new NPPLegal(), new NunzianteMagrone(), new NysinghAdvocatenNotarissenNV(), new Odigo(),
            new Orrick(), new PanettaConsultingGroup(), new PayetReyCauviPerez(), new Pedersoli(), new PelsRijcken(),
            new Penta(), new PFPLaw(), new Ploum(), new PopoviciNituStoicaAndAsociatii(), new PorwiszAndPartners(),
            new PricaAndPartners(), new ProskauerRose(), new RadulescuAndMusoi(), new RBK(), new RocaJunyent(),
            new RoedlAndPartner(), new Roschier(), new RPCLegal(), new SchindlerAttorneys(), new SchurtiPartners(),
            new SelihAndPartnerji(), new Selmer(), new Sidley(), new SIRIUS(), new Stibbe(),
            new StoneKing(), new SuarezDeVivero(), new ThomasBodstrom(), new Titov(), new TucaZbarcea(),
            new VanDerPutt(), new VBAdvocates(), new VieringJentschuraAndPartner(), new Vinge(), new VOPatentsAndTrademarks(),
            new WolfTheiss(), new ZamfirescuRacotiPredoiu(),

            // North America
//            new BarristonLaw(), new BennettJones(), new BLGLaw(), new BurnetDuckworthAndPalmer(), new Cassels(),
//            new ClarkWilson(), new DaleAndLessmann(), new DeethWilliamsWall(), new DillonEustace(), new ECLegalRubio(),
//            new FillmoreRiley(), new FoglerRubinoff(), new Goodmans(), new HNA(), new Langlois(),
//            new LawsonLundell(), new LEGlobal(), new LoopstraNixon(), new McDougallGauley(), new McKercher(),
//            new MijaresAngoitiaCortesAndFuentes(), new MLTAikins(), new NautaDutilh(), new NelliganLaw(), new OslerHoskinAndHarcourt(),
//            new OyenWiggs(), new RitchMueller(), new RitchMuellerAndNicolau(), new SantamarinaAndSteta(), new SmartAndBiggar(),
//            new StikemanElliott(), new VillarrealVGF(), new WildeboerDellelce(),

            // Central America
//            new GarciaBodan(), new McConnellValdes(),

            // South America
//            new AguayoEcclefieldAndMartinez(), new AlvarezAbogados(), new Andersen(), new BarriosAndFuentes(), new BeccarVarela(),
//            new Bermudes(), new BullrichFlanzbaum(), new CARAdvogados(), new CEPDAbogados(), new ChevezRuizZamarripa(),
//            new FarrocoAbreuGuarnieriZotelli(), new HernandezAndCia(), new LEFOSSE(), new MUC(), new NelsonWiliansAndAdvogados(),
//            new PayetReyCauviPerez(), new PayetReyCauviPerez(), new PPOAbogados(), new RennoPenteadoSampaioAdvogados(), new RobortellaEPeres(),
//            new Tavares(),

            // Oceania
            new AiGroup(), new AndersonLloyd(), new AnthonyHarper(), new BankiHaddockFiora(), new BuddleFindlay(),
            new DBHLaw(), new DuncanCotterill(), new JamesAndWells(), new MacphersonKelley(), new MalleyAndCo(),
            new MBIP(), new MinterEllisonRuddWatts(),

            // Mundial
            new ABAndDavid(), new AddleshawGoddardLLP(), new AlTamimi(), new Allens(), new ApplebyGlobal(),
            new ArnoldAndPorter(), new Ashurst(), new AVMAdvogados(), new AWA(), new BCLPLaw(),
            new Blakes(), new BNT(), new CareyOlsen(), new CassidyLevyKent(), new Chattertons(),
            new ClearyGottlieb(), new CliffordChance(), new Conyers(), new CovingtonAndBurlingLLP(), new CrowellAndMoring(),
            new DCCLaw(), new DebevoiseAndPlimpton(), new DuaneMorris(), new FaegreDrinkerBiddleAndReath(), new FRA(),
            new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodwin(), new GreenbergTraurig(), new HakunLaw(),
            new HavelPartners(), new HaynesAndBoone(), new HBNLaw(), new HerbertSmithFreehillsKramer(), new HFW(),
            new HillDickinson(), new HollandAndKnight(), new HuntonAndrewsKurth(), new JonesDay(), new JSKAdvokatni(),
            new Kinstellar(), new KISCHIP(), new LathamAndWatkins(), new MagnussonLaw(), new MayerBrown(),
            new McCarthyTetrault(), new Milbank(), new MillerThomsonLLP(), new MorganLewis(), new Ogier(),
            new Ogletree(), new PAGBAM(), new PaulHastings(), new PeterAndKim(), new PeterkaAndPartners(),
            new PillsburyWinthropShawPittman(), new RelianceCorporateAdvisors(), new RopesAndGray(), new Schoenherr(), new Secretariat(),
            new SheppardMullin(), new Skadden(), new SpencerWest(), new SprusonAndFerguson(), new SquirePattonBoggs(),
            new StephensonHarwood(), new StewartMcKelvey(), new SullivanAndWorcester(), new TahotaLaw(), new TaylorWessing(),
            new Walkers(), new WatsonFarleyAndWilliams(), new WhiteAndCase(), new WilliamFry(), new Willkie(),
            new WinstonAndStrawn(),
    };

    public final static Site[] byNewPage = {
            /* Firms to avoid
            new ALGoodbody(), new ArthurCox(), new Dentons(), new MishconKaras(), new OsborneClarke(),
            */

            // Africa
            new ENSAfrica(), new HansOffiaAndAssociates(), new JacksonEttiAndEdu(), new Shalakany(), new Werksmans(),
            new ZakiHashemAndPartners(), new ZulficarAndPartners(),

            // Asia
            new AOil(), new BarneaAndCo(), new CovenantChambers(), new DSKLegal(), new EBN(),
            new Fischer(), new GornitzkyAndCo(), new HuiyeLaw(), new HYLeungAndCo(), new JSA(),
            new KRBLaw(), new LonganLaw(), new MishconKaras(), new Mourant(), new NurmansyahAndMuzdalifah(),
            new RemfryAndSagar(), new SamvadPartners(), new TannerDeWitt(), new TCLaw(),

            // Europe
            new ABGIP(), new ACAndR(), new Astrea(), new Avance(), new BadriAndSalimElMeouchiLaw(),
            new Beauchamps(), new Belgravia(), new Borenius(), new Boyanov(), new BRAUNEISRECHTSANWALTE(),
            new BurgesSalmon(), new CarneluttiLaw(), new CRCCD(), new DANUBIAPatentAndLaw(), new DGKV(),
            new DKGV(), new Dottir(), new EisenfuhrSpeiserAndPartner(), new Ekelmans(), new EllisonsSolicitors(),
            new EPAndC(), new FCMLimited(), new FPSLaw(), new FrancisWilksAndJones(), new Frontier(),
            new Fylgia(), new GittiAndPartners(), new Goerg(), new GORG(), new GreeneAndGreeneSolicitors(),
            new Hamso(), new HarteBavendamm(), new HaslingerNagele(), new HoffmannEitle(), new Holmes(),
            new Horten(), new JadekAndPensa(), new JBLaw(), new JWP(), new KallioLaw(),
            new KambourovAndPartners(), new KBVLLaw(), new KennedyVanderLaan(), new KeystoneLaw(), new KLCLaw(),
            new Kondrat(), new Kvale(), new KWKRLaw(), new LambadariosLaw(), new LangsethAdvokat(),
            new LePooleBekema(), new Legalis(), new Lindahl(), new MaikowskiAndNinnemann(), new MeyerKoring(),
            new MorrisLaw(), new MSBSolicitors(), new NESTOR(), new OgletreeDeakins(), new OneEssexCourt(),
            new Onsagers(), new ONVLaw(), new Oppenheim(), new Oxera(), new PhilippeAndPartners(),
            new PinneyTalfourdSolicitors(), new PMP(), new PortaAndConsulentiAssociati(), new Poulschmith(), new PrinzAndPartner(),
            new PrueferAndPartner(), new PuschWahlig(), new Racine(), new ReinhardSkuhraWeiseAndPartnerGbR(), new RonanDalyJermyn(),
            new RymarzZdortMaruta(), new SampsonCowardLLP(), new SBGK(), new Schoups(), new SKWSchwarz(),
            new SlaughterAndMay(), new SRSLegal(), new Strelia(), new SZA(), new Szecskay(),
            new Valfor(), new VanOlmenAndWynant(), new Vischer(), new VossiusAndPartner(), new WardynskiAndPartners(),
            new Wiersholm(), new WikborgRein(), new ZeposAndYannopoulos(),

            // North America
//            new ArthurCox(), new AsafoAndCo(), new BCFLaw(), new BWBLLP(), new FilionWakelyThorupAngeletti(),
//            new IbanezParkman(), new KuriBrena(), new MBM(), new NaderHayauxAndGoebel(), new PrasadAndCompany(),
//            new Sangra(), new ThompsonDorfmanSweatman(), new VazquezTerceroAndZepeda(),

            // Central America
//            new MyersFletcherAndGordon(),

            // South America
//            new BrigrardUrrutia(), new CariolaDiezPerezCotapos(), new Ferrere(), new LatinAlliance(), new Madrona(),
//            new RMADVAdvogados(), new SargentAndKrahn(), new ZBV(),

            // Oceania
            new Baumgartners(), new DeutschMiller(), new DWFoxTucker(), new Gadens(), new SimmonsWolfhagen(),
            new TompkinsWake(), new YoungList(),

            // Mundial
            new ALGoodbody(), new BDO(), new CerhaHempel(), new Cobalt(), new ControlRisks(),
            new Cuatrecasas(), new Curtis(), new Dentons(), new EProint(), new Ellex(),
            new FangdaPartners(), new GuantaoLaw(), new HiggsAndJohnson(), new Houthoof(), new JohnsonCamachoAndSingh(),
            new JPMAndPartners(), new LatamLex(), new Legance(), new LexCaribbean(), new LewissSilkin(),
            new MarksAndClerk(), new MdME(), new METIDA(), new Noerr(), new OsborneClarke(),
            new PearlCohen(), new PortolanoCavallo(), new Pulegal(), new QuinEmanuel(), new SabaAndCo(),
            new SimmonsAndSimmons(), new Sorainen(), new TEMPLARS(), new Thommessen(), new Vaneps(),
            new WALLESS()
    };

    public final static Site[] byFilter = {};

    public final static Site[] byClick = {};

    public final static Site[] toTest = new Site[]{};
}