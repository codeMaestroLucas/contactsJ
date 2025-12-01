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
            new ABAndDavid(), new AGPAdvokater(), new Aera(), new Alliotts(), new AlukoAndOyebode(),
            new Amorys(), new ApplebyGlobal(), new AraozAndRueda(), new ArnesenIP(), new AronTadmorLevy(),
            new Ashitiva(), new AWA(), new BAHR(), new BancilaDiaconuSiAsociatii(), new BankiHaddockFiora(),
            new BARDEHLEPAGENBERG(), new BARENTSKRANS(), new BarristonLaw(), new BennettJones(), new BentsiEnchillLetsaAndAnkomah(),
            new BLGLaw(), new BlandyAndBlandy(), new BlakeMorgan(), new Blakes(), new BNT(),
            new BonelliErede(), new BoodleHatfield(), new BrinkmannAndPartner(), new Broseta(), new BullAndCo(),
            new BurnetDuckworthAndPalmer(), new BussMurtonLaw(), new ByrneWallace(), new BYRO(), new CampbellsLegal(),
            new CareyOlsen(), new Cassels(), new CassidyLevyKent(), new CastrenAndSnellman(), new CBA(),
            new Chattertons(), new Cirio(), new ClarkWilson(), new Clarkslegal(), new ClemensLaw(),
            new CLPLaw(), new Codex(), new CollasCrill(), new Conyers(), new CWAAssociates(),
            new DahlLaw(), new DaleAndLessmann(), new Delcade(), new DillonEustace(), new DimitrovPetrovAndCo(),
            new DittmarAndIndrenius(), new Dompatent(), new DrzewieckiTomaszek(), new DZPLaw(), new ECLegalRubio(),
            new Elverdam(), new Fidal(), new FilipAndCompany(), new FillmoreRiley(), new Finreg360(),
            new FisherQuarmbyAndPfeifer(), new FIVERS(), new FlichyGrange(), new FluegelPreissner(), new FoglerRubinoff(),
            new Foyen(), new FRA(), new GanadoAdvocates(), new GarciaBodan(), new GianniAndOrigoni(),
            new GomezAceboAndPombo(), new Goodmans(), new Haavind(), new HabrakenRutten(), new HammarskioldAndCo(),
            new HannesSnellman(), new HavelPartners(), new Hayes(), new HaynesAndBoone(), new HBNLaw(),
            new HFAndCo(), new HFW(), new HillDickinson(), new HjulmandCaptain(), new HNA(),
            new HollandAndKnight(), new Holst(), new HPPAttorneys(), new Hugel(), new HuntonAndrewsKurth(),
            new JGSA(), new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new Kanter(), new KantorAndImmerman(),
            new KewLaw(), new KienhuisLegal(), new Kinstellar(), new KISCHIP(), new KnezovicAndAssociates(),
            new KochanskiAndPartners(), new Kolster(), new Krogerus(), new Langlois(), new LaszczukAndWspolnicy(),
            new LawsonLundell(), new LEFOSSE(), new LEGlobal(), new LemstraVanDerKorst(), new LEXIA(),
            new LEXLogmannsstofa(), new Liedekerke(), new Logos(), new LoopstraNixon(), new LPAGGV(),
            new Lydian(), new MagnussonLaw(), new MAQS(), new Matheson(), new MayerBrown(),
            new MazantiAndersen(), new MccannFitzGerald(), new McCarthyTetrault(), new McConnellValdes(), new McDougallGauley(),
            new McKercher(), new MeitarLaw(), new MerilampiAttorneys(), new MijaresAngoitiaCortesAndFuentes(),
            new MitelAndAsociatii(), new MLTAikins(), new MoalemWeitemeyer(), new Molinari(), new MorogluArseven(),
            new MSP(), new NelliganLaw(), new NelsonWiliansAndAdvogados(), new NielsenNorager(), new Njord(),
            new NOEWE(), new Norens(), new NovaLaw(), new NPPLegal(), new NysinghAdvocatenNotarissenNV(),
            new Odigo(), new Ogier(), new Ogletree(), new OslerHoskinAndHarcourt(), new OyenWiggs(),
            new Paksoy(), new PanettaConsultingGroup(), new Pedersoli(), new Penta(), new PeterAndKim(),
            new PeterkaAndPartners(), new PFPLaw(), new PillsburyWinthropShawPittman(), new Ploum(), new PopoviciNituStoicaAndAsociatii(),
            new PorwiszAndPartners(), new PricaAndPartners(), new ProskauerRose(), new RadulescuAndMusoi(), new RBK(),
            new RitchMueller(), new RitchMuellerAndNicolau(), new RobortellaEPeres(), new RoedlAndPartner(), new RonanDalyJermyn(),
            new Roschier(), new RPCLegal(), new SantamarinaAndSteta(), new SchindlerAttorneys(), new Schoenherr(),
            new SchurtiPartners(), new Secretariat(), new SelihAndPartnerji(), new Selmer(), new ShahidLaw(),
            new SHorowitzAndCo(), new SheppardMullin(), new SIRIUS(), new SmartAndBiggar(), new SpencerWest(),
            new SquirePattonBoggs(), new STBB(), new SteinmetzHaringGurman(), new StewartMcKelvey(), new SuarezDeVivero(),
            new SullivanAndWorcester(), new Tavares(), new TheartMey(), new ThomasBodstrom(), new Titov(),
            new TucaZbarcea(), new VanDerPutt(), new VBAdvocates(), new VieringJentschuraAndPartner(), new Vinge(),
            new VOPatentsAndTrademarks(), new Walkers(), new WatsonFarleyAndWilliams(), new WhiteAndCase(), new WildeboerDellelce(),
            new WilliamFry(), new Willkie(), new WinstonAndStrawn(), new WolfTheiss(), new ZamfirescuRacotiPredoiu(),
            new JSKAdvokatni(), new Kallan(), new KECOLegal(), new MooreLegalKovacs(), new MVJMarkovicVukoticJovkovic(),
    };

    public final static Site[] byNewPage = {
            /* Firms to avoid
            new ALGoodbody(), new ArthurCox(), new Dentons(), new MishconKaras(), new OsborneClarke(),
            */
            new ABGIP(), new ACAndR(), new AOil(), new AsafoAndCo(), new Astrea(),
            new BDO(), new BCFLaw(), new Beauchamps(), new Belgravia(), new BarneaAndCo(),
            new Borenius(), new BRAUNEISRECHTSANWALTE(), new BWBLLP(), new BurgesSalmon(), new CarneluttiLaw(),
            new CerhaHempel(), new Cobalt(), new ControlRisks(), new CRCCD(), new Cuatrecasas(),
            new Curtis(), new DANUBIAPatentAndLaw(), new DGKV(), new DKGV(), new Dottir(),
            new EBN(), new Ekelmans(), new EisenfuhrSpeiserAndPartner(), new Ellex(), new EllisonsSolicitors(),
            new ENSAfrica(), new EPAndC(), new Esche(), new FCMLimited(), new Ferrere(),
            new FilionWakelyThorupAngeletti(), new Fischer(), new FPSLaw(), new Frontier(), new Fylgia(),
            new GittiAndPartners(), new Goerg(), new GORG(), new GornitzkyAndCo(), new GreeneAndGreeneSolicitors(),
            new Hamso(), new HansOffiaAndAssociates(), new HarteBavendamm(), new HaslingerNagele(),
            new HiggsAndJohnson(), new HoffmannEitle(), new Holmes(), new Horten(), new Houthoof(),
            new IbanezParkman(), new JacksonEttiAndEdu(), new JadekAndPensa(), new JBLaw(), new JWP(),
            new JPMAndPartners(), new KambourovAndPartners(), new KBVLLaw(), new KennedyVanderLaan(), new KeystoneLaw(),
            new KLCLaw(), new Kondrat(), new KRBLaw(), new KuriBrena(), new Kvale(),
            new LambadariosLaw(), new LangsethAdvokat(), new LatamLex(), new Legalis(), new Legance(),
            new LePooleBekema(), new LewissSilkin(), new LexCaribbean(), new Lindahl(), new MarksAndClerk(),
            new MaikowskiAndNinnemann(), new MBM(), new MdME(), new METIDA(), new MeyerKoring(),
            new MillerThomsonLLP(), new MitelAndAsociatii(), new MorrisLaw(), new Mourant(), new MSBSolicitors(),
            new MyersFletcherAndGordon(), new NaderHayauxAndGoebel(), new NESTOR(), new Noerr(), new OgletreeDeakins(),
            new OneEssexCourt(), new Onsagers(), new Oppenheim(), new Oxera(),
            new PearlCohen(), new PhilippeAndPartners(), new PinneyTalfourdSolicitors(), new PMP(), new PortaAndConsulentiAssociati(),
            new PortolanoCavallo(), new PrasadAndCompany(), new PrinzAndPartner(), new PrueferAndPartner(), new QuinEmanuel(),
            new Racine(), new RocaJunyent(), new RymarzZdortMaruta(), new SampsonCowardLLP(), new Sangra(),
            new SargentAndKrahn(), new SBGK(), new Schoups(), new Shalakany(), new Sherrards(),
            new SKWSchwarz(), new SlaughterAndMay(), new Sorainen(), new SRSLegal(), new Strelia(),
            new SZA(), new TEMPLARS(), new Thommessen(), new ThompsonDorfmanSweatman(), new Valfor(),
            new Vaneps(), new VanOlmenAndWynant(), new VazquezTerceroAndZepeda(), new Vischer(),
            new VossiusAndPartner(), new WALLESS(), new WardynskiAndPartners(), new Werksmans(), new Wiersholm(),
            new WikborgRein(), new ZakiHashemAndPartners(), new ZeposAndYannopoulos(), new ZulficarAndPartners(), new ONVLaw(),
            new JohnsonCamachoAndSingh(), new KWKRLaw(),
    };

    public final static Site[] byFilter = {};

    public final static Site[] byClick = {};
}