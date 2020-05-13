function ViewRnrMmiaController($scope, $route, Requisitions, messageService, downloadPdfService, downloadSimamService) {
    $scope.rnrLineItems = [];
    $scope.regimens = [];
    $scope.regimeTotalPatients = 0;
    $scope.regimeTotalComunitaryPharmacy = 0;
    $scope.therapeuticLines = [];
    $scope.therapeuticLinesTotalPatients = 0;
    $scope.therapeuticLinesTotalComunitaryPharmacy = 0;

    $scope.patient = [];

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadMmiaDetail();
    });

    $scope.$on('messagesPopulated', function () {
        $scope.initMonth();
    });

    $(".btn-download-pdf").hide();
    $(".btn-download-simam").hide();
    $scope.loadMmiaDetail = function () {
        Requisitions.get({ id: $route.current.params.rnr, operation: "skipped" }, function (data) {
            $scope.rnr = data.rnr;
            $scope.year = data.rnr.period.stringYear;

            $scope.initMonth();
            $scope.initProduct();
            $scope.initRegime();
            if (data.rnr.patientQuantifications.length == 7) {
                $scope.initOldPatient();
            } else {
                $scope.initPatient();
            }
            $scope.initTherapeuticLines();
            parseSignature($scope.rnr.rnrSignatures);

            downloadPdfService.init($scope, $scope.rnr.id);
            downloadSimamService.init($scope, $scope.rnr.id);
        });
    };

    function parseSignature(signatures) {
        _.forEach(signatures, function (signature) {
            if (signature.type == "SUBMITTER") {
                $scope.submitterSignature = signature.text;
            } else if (signature.type == "APPROVER") {
                $scope.approverSignature = signature.text;
            }
        });
    }

    $scope.initTherapeuticLines = function () {

        var therapeuticLinesMap = {
            '1st Line': 'view.rnr.mmia.regime.therapeutic.line.1',
            '1ª Linha': 'view.rnr.mmia.regime.therapeutic.line.1',
            '2nd Line': 'view.rnr.mmia.regime.therapeutic.line.2',
            '2ª Linha': 'view.rnr.mmia.regime.therapeutic.line.2',
            '3rd Line': 'view.rnr.mmia.regime.therapeutic.line.3',
            '3ª Linha': 'view.rnr.mmia.regime.therapeutic.line.3'
        };
        $scope.therapeuticLines = _.map($scope.rnr.therapeuticLines,
            function (value) {
                value.code = therapeuticLinesMap[value.code];
                return value;
            });
    };

    $scope.initMonth = function () {
        var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
        $scope.month = messageService.get(month);
    };

    function addEmptyLine(fullSupplyLineItems) {

        fullSupplyLineItems = _.groupBy(fullSupplyLineItems, function (item) {
            return item.categoryName;
        });

        if (fullSupplyLineItems.Adult === undefined) {
            fullSupplyLineItems.Adult = [];
        }

        if (fullSupplyLineItems.Children === undefined) {
            fullSupplyLineItems.Children = [];
        }

        if (fullSupplyLineItems.Solution === undefined) {
            fullSupplyLineItems.Solution = [];
        }

        fullSupplyLineItems.Adult.push({ categoryName: 'Adult' });
        fullSupplyLineItems.Adult.push({ categoryName: 'Adult' });
        fullSupplyLineItems.Children.push({ categoryName: 'Children' });
        fullSupplyLineItems.Solution.push({ categoryName: 'Solution' });

        $scope.rnrLineItems = $scope.rnrLineItems.concat(fullSupplyLineItems.Adult, fullSupplyLineItems.Children, fullSupplyLineItems.Solution);
    }

    $scope.initProduct = function () {
        var fullSupplyLineItems = _.sortBy($scope.rnr.fullSupplyLineItems, 'productCode');

        for (var i = 0; i < fullSupplyLineItems.length; i++) {
            formatExpirationDate(fullSupplyLineItems[i]);
        }

        addEmptyLine(fullSupplyLineItems);
    };



    $scope.initOldPatient = function () {
        var patientQuantifications = $scope.rnr.patientQuantifications;
        var openlmisMessageMap = {
            "New": "new",
            "Maintenance": "maintenance",
            "Alteration": "alteration",
            "PTV": "ptv",
            "PPE": "ppe",
            "Total Dispensed": "dispensed",
            "Total Patients": "total",
            "Novos": "new",
            "Manutenção": "maintenance",
            "Alteração": "alteration",
            "Total de Meses dispensados": "dispensed",
            "Total de pacientes em TARV na US": "total"
        };
        for (var i = 0; i < patientQuantifications.length; i++) {
            var item = patientQuantifications[i];
            item.category = "view.rnr.mmia.patient." + openlmisMessageMap[item.category];
        }
        $scope.rnr.reportType="old";
    };

    $scope.initPatient = function () {
        var openlmisMessageMap = {
            "new": { messageName: "new", tableName: 'Type of patients in TARV' },
            "novos": { messageName: "new", tableName: 'Type of patients in TARV' },
            "maintenance": { messageName: "maintenance", tableName: 'Type of patients in TARV' },
            "manutenção": { messageName: "maintenance", tableName: 'Type of patients in TARV' },
            "alteration": { messageName: "alteration", tableName: 'Type of patients in TARV' },
            "alteração": { messageName: "alteration", tableName: 'Type of patients in TARV' },
            "transit": { messageName: "transit", tableName: 'Type of patients in TARV' },
            "trânsito": { messageName: "transit", tableName: 'Type of patients in TARV' },
            "transfers": { messageName: "transfers", tableName: 'Type of patients in TARV' },
            "transferências": { messageName: "transfers", tableName: 'Type of patients in TARV' },
            "3 mopnths dispense (dt)": { messageName: "3dt", tableName: 'Months dispensed' },
            "dispensa para 3 meses (dt)": { messageName: "3dt", tableName: 'Months dispensed' },
            "month dispense": { messageName: "md", tableName: 'Months dispensed' },
            "mês dispensado": { messageName: "md", tableName: 'Months dispensed' },
            "# of therapeutic months dispensed": { messageName: "therapeuticDt", tableName: 'Months dispensed' },
            "# de meses terapêuticos dispensados": { messageName: "therapeuticDt", tableName: 'Months dispensed' },
            "adults": { messageName: "adults", tableName: 'age range of TARV patients' },
            "adultos": { messageName: "adults", tableName: 'age range of TARV patients' },
            "pediatric from 0 to 4 years": { messageName: "4pediatric", tableName: 'age range of TARV patients' },
            "pediátricos 0 aos 4 anos": { messageName: "4pediatric", tableName: 'age range of TARV patients' },
            "pediatric from 5 to 9 years": { messageName: "9pediatric", tableName: 'age range of TARV patients' },
            "pediátricos 5 aos 9 anos": { messageName: "9pediatric", tableName: 'age range of TARV patients' },
            "pediatric from 10 to 14 years": { messageName: "14pediatric", tableName: 'age range of TARV patients' },
            "pediátricos 10 aos 14 anos": { messageName: "14pediatric", tableName: 'age range of TARV patients' },
            "prep": { messageName: "prep", tableName: 'prophylaxis' },
            "ppe": { messageName: "ppe", tableName: 'prophylaxis' },
            "exposed child": { messageName: "exposed", tableName: 'prophylaxis' },
            "criança exposta": { messageName: "exposed", tableName: 'prophylaxis' },
            "total nr of patients in tarv at hf": { messageName: "totalNr", tableName: 'prophylaxis' },
            "total de pacientes em tarv na us": { messageName: "totalNr", tableName: 'prophylaxis' },
        };
        var tableMap = {
            'Type of patients in TRAV': 'patientsType',
            'Tipo de pacientes em TARV': 'patientsType',
            'Months dispensed': 'md',
            'Meses dispensados': 'md',
            'age range of TRAV patients': 'TARVPatients',
            'Faixa etária dos pacientes em TRAV': 'TARVPatients',
            'prophylaxis': 'prophylaxis',
            'Profilaxia': 'prophylaxis'
        };
        var invalideKey = _.keys(openlmisMessageMap);
        var patientQuantifications = _.filter($scope.rnr.patientQuantifications, function (value) {
            var lowerCategory = value.category && value.category.toLocaleLowerCase() || '';
            return _.includes(invalideKey, lowerCategory);
        });
        patientQuantifications = _.map(patientQuantifications, function (value) {
            var category = openlmisMessageMap[value.category.toLocaleLowerCase()];
            var tableName = value.tableName ? value.tableName : category.tableName;
            return _.assign({}, value, {
                header: "view.rnr.mmia.patient.header." + tableMap[tableName],
                messageName: "view.rnr.mmia.patient." + category.messageName,
            });
        });
        patientQuantifications = _.groupBy(patientQuantifications, 'header');
        $scope.rnr.patientQuantifications = patientQuantifications;
        $scope.rnr.reportType = "new";
    };

    var formatExpirationDate = function (theOneItem) {
        if (theOneItem.expirationDate) {
            var splitDate = theOneItem.expirationDate.split('/');
            var yearNumber = splitDate[2];
            var monthNumber = splitDate[1];

            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            ];

            theOneItem.expirationDate = monthNames[monthNumber - 1] + " " + yearNumber;
        }
    };


    $scope.initRegime = function () {
        var regimens = _.groupBy($scope.rnr.regimenLineItems, function (item) {
            return item.categoryName;
        });

        if (regimens.Adults === undefined) {
            regimens.Adults = [];
        }

        if (regimens.Paediatrics === undefined) {
            regimens.Paediatrics = [];
        }

        regimens.Adults.push({ categoryName: 'Adults' });
        regimens.Adults.push({ categoryName: 'Adults' });

        regimens.Paediatrics.push({ categoryName: 'Paediatrics' });
        regimens.Paediatrics.push({ categoryName: 'Paediatrics' });

        $scope.regimens = $scope.regimens.concat(regimens.Adults, regimens.Paediatrics);
        calculateRegimeTotalPatients($scope.rnr.regimenLineItems);
        if ($scope.rnr.regimenLineItems[0].comunitaryPharmacy !== undefined) {
            calculateRegimeTotalComunitaryPharmacy($scope.rnr.regimenLineItems);
        }
        if($scope.rnr.therapeuticLines !== undefined){
            calculateTherapeuticLinesTotalPatients($scope.rnr.therapeuticLines);
            calculateTherapeuticLinesTotalComunitaryPharmacy($scope.rnr.therapeuticLines);
        }
    };

    var calculateRegimeTotalPatients = function (regimens) {
        for (var i = 0; i < regimens.length; i++) {
            $scope.regimeTotalPatients += regimens[i].patientsOnTreatment;
        }
    };

    var calculateRegimeTotalComunitaryPharmacy = function (regimens) {
        for (var i = 0; i < regimens.length; i++) {
            $scope.regimeTotalComunitaryPharmacy += regimens[i].comunitaryPharmacy;
        }
    };

    var calculateTherapeuticLinesTotalPatients = function (therapeuticLines) {
        for (var i = 0; i < therapeuticLines.length; i++) {
            $scope.therapeuticLinesTotalPatients += therapeuticLines[i].patientsOnTreatment;
        }
    };

    var calculateTherapeuticLinesTotalComunitaryPharmacy = function (therapeuticLines) {
        for (var i = 0; i < therapeuticLines.length; i++) {
            $scope.therapeuticLinesTotalComunitaryPharmacy += therapeuticLines[i].comunitaryPharmacy;
        }
    };
}
